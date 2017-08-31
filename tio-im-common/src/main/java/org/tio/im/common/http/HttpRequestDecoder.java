package org.tio.im.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.exception.AioDecodeException;
import org.tio.im.common.http.HttpConst.RequestBodyFormat;

import com.xiaoleilu.hutool.util.StrUtil;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpRequestDecoder {
	private static Logger log = LoggerFactory.getLogger(HttpRequestDecoder.class);

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public HttpRequestDecoder() {

	}

	public static final int MAX_HEADER_LENGTH = 20480;

	public static HttpRequestPacket decode(ByteBuffer buffer,boolean isBody) throws AioDecodeException {
		int count = 0;
		Step step = Step.firstline;
		StringBuilder currLine = new StringBuilder();
		Map<String, String> headers = new HashMap<>();
		int contentLength = 0;
		byte[] bodyBytes = null;
		RequestLine firstLine = null;
		while (buffer.hasRemaining()) {
			count++;
			if (count > MAX_HEADER_LENGTH) {
				throw new AioDecodeException("max http header length " + MAX_HEADER_LENGTH);
			}

			byte b = buffer.get();

			if (b == '\n') {
				if (currLine.length() == 0) {
					String contentLengthStr = headers.get(HttpConst.RequestHeaderKey.Content_Length);
					if (StringUtils.isBlank(contentLengthStr)) {
						contentLength = 0;
					} else {
						contentLength = Integer.parseInt(contentLengthStr);
					}

					int readableLength = buffer.limit() - buffer.position();
					if (readableLength >= contentLength) {
						step = Step.body;
						break;
					} else {
						return null;
					}
				} else {
					if (step == Step.firstline) {
						firstLine = parseRequestLine(currLine.toString());
						step = Step.header;
					} else if (step == Step.header) {
						KeyValue keyValue = parseHeaderLine(currLine.toString());
						headers.put(keyValue.getKey(), keyValue.getValue());
					}

					currLine.setLength(0);
				}
				continue;
			} else if (b == '\r') {
				continue;
			} else {
				currLine.append((char) b);
			}
		}

		if (step != Step.body) {
			return null;
		}

		if (!headers.containsKey(HttpConst.RequestHeaderKey.Host)) {
			throw new AioDecodeException("there is no host header");
		}

		HttpRequestPacket httpRequestPacket = new HttpRequestPacket();
		
		httpRequestPacket.setRequestLine(firstLine);
		httpRequestPacket.setHeaders(headers);
		if(isBody){
			if (contentLength > 0) {
				bodyBytes = new byte[contentLength];
				buffer.get(bodyBytes);
			}
			httpRequestPacket.setContentLength(contentLength);
			
			if (contentLength == 0) {
				if (StringUtils.isNotBlank(firstLine.getQueryStr())) {
					Map<String, Object[]> params = decodeParams(firstLine.getQueryStr(), httpRequestPacket.getCharset());
					httpRequestPacket.setParams(params);
				}
			}
			//解析消息体
			parseBody(httpRequestPacket, firstLine, bodyBytes);
		}
		return httpRequestPacket;

	}
	/**
	 * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
	 * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
	 * @param httpRequestPacket
	 * @param headers
	 * @author: tanyaowu
	 */
	public static void parseBodyFormat(HttpRequestPacket httpRequestPacket, Map<String, String> headers) {
		String Content_Type = StringUtils.lowerCase(headers.get(HttpConst.RequestHeaderKey.Content_Type));
		RequestBodyFormat bodyFormat = null;
		if (StringUtils.contains(Content_Type, HttpConst.RequestHeaderValue.Content_Type.application_x_www_form_urlencoded)) {
			bodyFormat = RequestBodyFormat.URLENCODED;
		} else if (StringUtils.contains(Content_Type, HttpConst.RequestHeaderValue.Content_Type.multipart_form_data)) {
			bodyFormat = RequestBodyFormat.MULTIPART;
		} else {
			bodyFormat = RequestBodyFormat.TEXT;
		}
		httpRequestPacket.setBodyFormat(bodyFormat);

		if (StringUtils.isNoneBlank(Content_Type)) {
			String[] ss = StringUtils.split(Content_Type, ";");
			if (ss.length > 1) {
				for (String str : ss) {
					String[] ss1 = StringUtils.split(str, "=");
					if (ss1.length > 1) {
						String key = ss1[0];
						String value = ss1[1];
						if (StringUtils.endsWithIgnoreCase(key, "charset")) {
							httpRequestPacket.setCharset(value);
							log.info("解析到charset:{}", value);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 解析消息体 TODO: MULTIPART待完成
	 * @param httpRequestPacket
	 * @param firstLine
	 * @param bodyBytes
	 * @author: tanyaowu
	 */
	private static void parseBody(HttpRequestPacket httpRequestPacket, RequestLine firstLine, byte[] bodyBytes) {
		if(bodyBytes == null)
			return;
		parseBodyFormat(httpRequestPacket, httpRequestPacket.getHeaders());
		RequestBodyFormat bodyFormat = httpRequestPacket.getBodyFormat();
		
		String bodyString = null;
		httpRequestPacket.setBody(bodyBytes);
		try {
			bodyString = new String(bodyBytes, httpRequestPacket.getCharset());
			httpRequestPacket.setBodyString(bodyString);
			log.info("bodyString:{}",bodyString);
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
		if (bodyFormat == RequestBodyFormat.URLENCODED) {
			parseUrlencoded(httpRequestPacket, firstLine, bodyBytes, bodyString);
		}
	}

	/**
	 * 解析URLENCODED格式的消息体
	 * 形如： 【Content-Type : application/x-www-form-urlencoded; charset=UTF-8】
	 * @author: tanyaowu
	 */
	private static void parseUrlencoded(HttpRequestPacket httpRequestPacket, RequestLine firstLine, byte[] bodyBytes, String bodyString) {
		String paramStr = "";
		if (StringUtils.isNotBlank(firstLine.getQueryStr())) {
			paramStr += firstLine.getQueryStr();
		}
		if (bodyString != null) {
			if (paramStr != null) {
				paramStr += "&";
			}
			paramStr += bodyString;
		}

		if (paramStr != null) {
			Map<String, Object[]> params = decodeParams(paramStr, httpRequestPacket.getCharset());
			httpRequestPacket.setParams(params);
//			log.error("paramStr:{}", paramStr);
//			log.error("param:{}", Json.toJson(params));
		}
	}
	
//	private static void parseText(HttpRequestPacket httpRequestPacket, RequestLine firstLine, byte[] bodyBytes, String bodyString) {
//		String paramStr = "";
//		if (StringUtils.isNotBlank(firstLine.getQueryStr())) {
//			paramStr += firstLine.getQueryStr();
//		}
//		if (bodyString != null) {
//			if (paramStr != null) {
//				paramStr += "&";
//			}
//			paramStr += bodyString;
//		}
//
//		if (paramStr != null) {
//			Map<String, List<String>> params = HttpUtil.decodeParams(paramStr, httpRequestPacket.getCharset());
//			httpRequestPacket.setParams(params);
//			log.error("paramStr:{}", paramStr);
//			log.error("param:{}", Json.toJson(params));
//		}
//	}



	/**
	 * 解析第一行(请求行)
	 * @param line
	 * @return
	 *
	 * @author: tanyaowu
	 * 2017年2月23日 下午1:37:51
	 *
	 */
	public static RequestLine parseRequestLine(String line) {
		int index1 = line.indexOf(' ');
		String _method = StringUtils.upperCase(line.substring(0, index1));
		Method method = Method.from(_method);
		int index2 = line.indexOf(' ', index1 + 1);
		String pathAndQuerystr = line.substring(index1 + 1, index2);   // "/user/get?name=999"
		String path = null;   //"/user/get"
		String queryStr = null;
		int indexOfQuestionmark = pathAndQuerystr.indexOf("?");
		if (indexOfQuestionmark != -1) {
			queryStr = StringUtils.substring(pathAndQuerystr, indexOfQuestionmark + 1);
			path = StringUtils.substring(pathAndQuerystr, 0, indexOfQuestionmark);
		} else {
			path = pathAndQuerystr;
			queryStr = "";
		}

		String version = line.substring(index2 + 1);

		RequestLine requestLine = new RequestLine();
		requestLine.setMethod(method);
		requestLine.setPath(path);
		requestLine.setPathAndQuerystr(pathAndQuerystr);
		requestLine.setQueryStr(queryStr);
		requestLine.setVersion(version);
		requestLine.setInitStr(line);
		
		
		
		
		
		return requestLine;
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @return
	 *
	 * @author: tanyaowu
	 * 2017年2月23日 下午1:37:58
	 *
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
	
	
	public static Map<String, Object[]> decodeParams(String paramsStr, String charset) {
		if (StrUtil.isBlank(paramsStr)) {
			return Collections.emptyMap();
		}

//		// 去掉Path部分
//		int pathEndPos = paramsStr.indexOf('?');
//		if (pathEndPos > 0) {
//			paramsStr = StrUtil.subSuf(paramsStr, pathEndPos + 1);
//		}
		Map<String, Object[]> ret = new HashMap<>();
		String[] keyvalues = StringUtils.split(paramsStr, "&");
		for(String keyvalue : keyvalues){
			String[] keyvalueArr = StringUtils.split(keyvalue, "=");
			if (keyvalueArr.length != 2) {
				continue;
			}
			
			String key = keyvalueArr[0];
			String value = null;
			try {
				value = URLDecoder.decode(keyvalueArr[1], charset);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
			
			Object[] existValue = ret.get(key);
			if (existValue != null) {
				String[] newExistValue = new String[existValue.length + 1];
				System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
				newExistValue[newExistValue.length - 1] = value;
				ret.put(key, newExistValue);
			} else {
				String[] newExistValue = new String[]{value};
				ret.put(key, newExistValue);
			}
		}
		return ret;
	}

	public static enum Step {
		firstline, header, body
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public static void main(String[] args) {

	}

}
