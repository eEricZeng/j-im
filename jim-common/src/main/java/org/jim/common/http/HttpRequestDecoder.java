package org.jim.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.http.HttpConst.RequestBodyFormat;
import org.jim.common.utils.HttpParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.exception.LengthOverflowException;
import org.tio.core.utils.ByteBufferUtils;

import cn.hutool.core.util.StrUtil;

/**
 *
 * @author WChao
 *
 */
public class HttpRequestDecoder {
	public static enum Step {
		firstLine, header, body
	}

	private static Logger log = LoggerFactory.getLogger(HttpRequestDecoder.class);

	/**
	 * 头部，最多有多少字节
	 */
	public static final int MAX_LENGTH_OF_HEADER = 20480;

	/**
	 * 头部，每行最大的字节数
	 */
	public static final int MAX_LENGTH_OF_HEADER_LINE = 2048;

	public static HttpRequest decode(ByteBuffer buffer, ChannelContext channelContext,boolean isBody) throws AioDecodeException {
		int initPosition = buffer.position();
		int readableLength = buffer.limit() - initPosition;
		//		int count = 0;
		Step step = Step.firstLine;
		//		StringBuilder currLine = new StringBuilder();
		Map<String, String> headers = new HashMap<>();
		int contentLength = 0;
		byte[] bodyBytes = null;
		StringBuilder headerSb = new StringBuilder(512);
		RequestLine firstLine = null;

		while (buffer.hasRemaining()) {
			String line;
			try {
				line = ByteBufferUtils.readLine(buffer, null, MAX_LENGTH_OF_HEADER_LINE);
			} catch (LengthOverflowException e) {
				throw new AioDecodeException(e);
			}

			int newPosition = buffer.position();
			if (newPosition - initPosition > MAX_LENGTH_OF_HEADER) {
				throw new AioDecodeException("max http header length " + MAX_LENGTH_OF_HEADER);
			}

			if (line == null) {
				return null;
			}

			headerSb.append(line).append("\r\n");
			//头部解析完成了
			if ("".equals(line) && isBody) {
				String contentLengthStr = headers.get(HttpConst.RequestHeaderKey.Content_Length);
				if (StringUtils.isBlank(contentLengthStr)) {
					contentLength = 0;
				} else {
					contentLength = Integer.parseInt(contentLengthStr);
				}

				int headerLength = (buffer.position() - initPosition);
				//这个packet所需要的字节长度(含头部和体部)
				int allNeedLength = headerLength + contentLength;
				if (readableLength >= allNeedLength) {
					step = Step.body;
					break;
				} else {
					channelContext.setPacketNeededLength(allNeedLength);
					return null;
				}
			} else {
				if (step == Step.firstLine) {
					firstLine = parseRequestLine(line, channelContext);
					step = Step.header;
				} else if (step == Step.header) {
					//不解析包体的话,结束(换句话说就是只解析请求行与请求头)
					if("".equals(line) && !isBody) {
						break;
					}
					KeyValue keyValue = parseHeaderLine(line);
					headers.put(keyValue.getKey(), keyValue.getValue());
				}
				continue;
			}
		}

		if (step != Step.body && isBody) {
			return null;
		}

		if (!headers.containsKey(HttpConst.RequestHeaderKey.Host)) {
			throw new AioDecodeException("there is no host header");
		}

		HttpRequest httpRequest = new HttpRequest(channelContext.getClientNode());
		httpRequest.setChannelContext(channelContext);
		httpRequest.setHttpConfig((HttpConfig) channelContext.getGroupContext().getAttribute(GroupContextKey.HTTP_SERVER_CONFIG));
		httpRequest.setHeaderString(headerSb.toString());
		httpRequest.setRequestLine(firstLine);
		httpRequest.setHeaders(headers);
		httpRequest.setContentLength(contentLength);

		parseQueryString(httpRequest, firstLine, channelContext);

		if (contentLength == 0) {

		} else {
			bodyBytes = new byte[contentLength];
			buffer.get(bodyBytes);
			httpRequest.setBody(bodyBytes);
			//解析消息体
			parseBody(httpRequest, firstLine, bodyBytes, channelContext);
		}
		return httpRequest;

	}

	public static void decodeParams(Map<String, Object[]> params, String paramsStr, String charset, ChannelContext channelContext) {
		if (StrUtil.isBlank(paramsStr)) {
			return;
		}
		String[] keyValues = StringUtils.split(paramsStr, "&");
		for (String keyValue : keyValues) {
			String[] keyValueArr = StringUtils.split(keyValue, "=");
			if (keyValueArr.length != 2) {
				continue;
			}

			String key = keyValueArr[0];
			String value = null;
			try {
				value = URLDecoder.decode(keyValueArr[1], charset);
			} catch (UnsupportedEncodingException e) {
				log.error(channelContext.toString(), e);
			}

			Object[] existValue = params.get(key);
			if (existValue != null) {
				String[] newExistValue = new String[existValue.length + 1];
				System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
				newExistValue[newExistValue.length - 1] = value;
				params.put(key, newExistValue);
			} else {
				String[] newExistValue = new String[] { value };
				params.put(key, newExistValue);
			}
		}
		return;
	}

	/**
	 * 解析消息体
	 * @param httpRequest
	 * @param firstLine
	 * @param bodyBytes
	 * @param channelContext
	 * @throws AioDecodeException
	 * @author WChao
	 */
	private static void parseBody(HttpRequest httpRequest, RequestLine firstLine, byte[] bodyBytes, ChannelContext channelContext) throws AioDecodeException {
		parseBodyFormat(httpRequest, httpRequest.getHeaders());
		RequestBodyFormat bodyFormat = httpRequest.getBodyFormat();

		httpRequest.setBody(bodyBytes);

		if (bodyFormat == RequestBodyFormat.MULTIPART) {
			if (log.isInfoEnabled()) {
				String bodyString = null;
				if (bodyBytes != null && bodyBytes.length > 0) {
					if (log.isDebugEnabled()) {
						try {
							bodyString = new String(bodyBytes, httpRequest.getCharset());
							log.debug("{} multipart body string\r\n{}", channelContext, bodyString);
						} catch (UnsupportedEncodingException e) {
							log.error(channelContext.toString(), e);
						}
					}
				}
			}

			//【multipart/form-data; boundary=----WebKitFormBoundaryuwYcfA2AIgxqIxA0】
			String initBoundary = HttpParseUtils.getPerprotyEqualValue(httpRequest.getHeaders(), HttpConst.RequestHeaderKey.Content_Type, "boundary");
			log.debug("{}, initBoundary:{}", channelContext, initBoundary);
			HttpMultiBodyDecoder.decode(httpRequest, firstLine, bodyBytes, initBoundary, channelContext);
		} else {
			String bodyString = null;
			if (bodyBytes != null && bodyBytes.length > 0) {
				try {
					bodyString = new String(bodyBytes, httpRequest.getCharset());
					httpRequest.setBodyString(bodyString);
					if (log.isInfoEnabled()) {
						log.info("{} body string\r\n{}", channelContext, bodyString);
					}
				} catch (UnsupportedEncodingException e) {
					log.error(channelContext.toString(), e);
				}
			}

			if (bodyFormat == RequestBodyFormat.URLENCODED) {
				parseUrlencoded(httpRequest, firstLine, bodyBytes, bodyString, channelContext);
			}
		}
	}

	/**
	 * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
	 * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
	 * @param httpRequest
	 * @param headers
	 * @author WChao
	 */
	public static void parseBodyFormat(HttpRequest httpRequest, Map<String, String> headers) {
		String Content_Type = StringUtils.lowerCase(headers.get(HttpConst.RequestHeaderKey.Content_Type));
		RequestBodyFormat bodyFormat = null;
		if (StringUtils.contains(Content_Type, HttpConst.RequestHeaderValue.Content_Type.application_x_www_form_urlencoded)) {
			bodyFormat = RequestBodyFormat.URLENCODED;
		} else if (StringUtils.contains(Content_Type, HttpConst.RequestHeaderValue.Content_Type.multipart_form_data)) {
			bodyFormat = RequestBodyFormat.MULTIPART;
		} else {
			bodyFormat = RequestBodyFormat.TEXT;
		}
		httpRequest.setBodyFormat(bodyFormat);

		if (StringUtils.isNotBlank(Content_Type)) {
			String charset = HttpParseUtils.getPerprotyEqualValue(headers, HttpConst.RequestHeaderKey.Content_Type, "charset");
			if (StringUtils.isNotBlank(charset)) {
				httpRequest.setCharset(charset);
			}
		}
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @return
	 * @author WChao
	 * 2017年2月23日 下午1:37:58
	 */
	public static KeyValue parseHeaderLine(String line) {
		KeyValue keyValue = new KeyValue();
		int p = line.indexOf(":");
		if (p == -1) {
			keyValue.setKey(line);
			return keyValue;
		}

		String name = StringUtils.lowerCase(line.substring(0, p).trim());
		String value = line.substring(p + 1).trim();

		keyValue.setKey(name);
		keyValue.setValue(value);

		return keyValue;
	}

	/**
	 * 解析第一行(请求行)
	 * @param line
	 * @param channelContext
	 * @return
	 *
	 * @author WChao
	 * 2017年2月23日 下午1:37:51
	 *
	 */
	public static RequestLine parseRequestLine(String line, ChannelContext channelContext) throws AioDecodeException {
		try {
			int index1 = line.indexOf(' ');
			String _method = StringUtils.upperCase(line.substring(0, index1));
			Method method = Method.from(_method);
			int index2 = line.indexOf(' ', index1 + 1);
			// "/user/get?name=999"
			String pathAndQueryStr = line.substring(index1 + 1, index2);
			//"/user/get"
			String path = null;
			String queryStr = null;
			int indexOfQuestionMark = pathAndQueryStr.indexOf("?");
			if (indexOfQuestionMark != -1) {
				queryStr = StringUtils.substring(pathAndQueryStr, indexOfQuestionMark + 1);
				path = StringUtils.substring(pathAndQueryStr, 0, indexOfQuestionMark);
			} else {
				path = pathAndQueryStr;
				queryStr = "";
			}

			String protocolVersion = line.substring(index2 + 1);
			String[] pv = StringUtils.split(protocolVersion, "/");
			String protocol = pv[0];
			String version = pv[1];

			RequestLine requestLine = new RequestLine();
			requestLine.setMethod(method);
			requestLine.setPath(path);
			requestLine.setInitPath(path);
			requestLine.setPathAndQuery(pathAndQueryStr);
			requestLine.setQuery(queryStr);
			requestLine.setVersion(version);
			requestLine.setProtocol(protocol);
			requestLine.setLine(line);

			return requestLine;
		} catch (Throwable e) {
			log.error(channelContext.toString(), e);
			throw new AioDecodeException(e);
		}
	}

	/**
	 * 解析URLENCODED格式的消息体
	 * 形如： 【Content-Type : application/x-www-form-urlencoded; charset=UTF-8】
	 * @author WChao
	 */
	private static void parseUrlencoded(HttpRequest httpRequest, RequestLine firstLine, byte[] bodyBytes, String bodyString, ChannelContext channelContext) {
		if (StringUtils.isNotBlank(bodyString)) {
			decodeParams(httpRequest.getParams(), bodyString, httpRequest.getCharset(), channelContext);
		}
	}

	/**
	 * 解析查询
	 * @param httpRequest
	 * @param firstLine
	 * @param channelContext
	 */
	private static void parseQueryString(HttpRequest httpRequest, RequestLine firstLine, ChannelContext channelContext) {
		String paramStr = firstLine.getQuery();
		if (StringUtils.isNotBlank(paramStr)) {
			decodeParams(httpRequest.getParams(), paramStr, httpRequest.getCharset(), channelContext);
		}
	}

	/**
	 *
	 * @author WChao
	 * 2017年2月22日 下午4:06:42
	 *
	 */
	public HttpRequestDecoder() {

	}

}
