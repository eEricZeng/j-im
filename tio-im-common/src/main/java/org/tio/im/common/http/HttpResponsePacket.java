package org.tio.im.common.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.ZipUtil;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpResponsePacket extends HttpPacket {
	
	
	private static final long serialVersionUID = -2415737699894208751L;

	private HttpRequestPacket httpRequestPacket = null;
	
	private List<Cookie> cookies = null;
	
	private String charset = HttpConst.CHARSET_NAME;
	/**
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 */
	public HttpResponsePacket(HttpRequestPacket httpRequestPacket) {
		this.httpRequestPacket = httpRequestPacket;
		this.status = HttpResponseStatus.C200;
		String Connection = StringUtils.lowerCase(httpRequestPacket.getHeader(HttpConst.RequestHeaderKey.Connection));
		if (StringUtils.equals(Connection, HttpConst.RequestHeaderValue.Connection.keep_alive)) {
			addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
			addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
		}
		addHeader(HttpConst.ResponseHeaderKey.Server, HttpConst.SERVER_INFO);
//		String xx = DatePattern.HTTP_DATETIME_FORMAT.format(SystemTimer.currentTimeMillis());
//		addHeader(HttpConst.ResponseHeaderKey.Date, DatePattern.HTTP_DATETIME_FORMAT.format(SystemTimer.currentTimeMillis()));
//		addHeader(HttpConst.ResponseHeaderKey.Date, new Date().toGMTString());
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public static void main(String[] args) {
	}

	public boolean addCookie(Cookie cookie) {
		if (cookies == null) {
			synchronized (this) {
				if (cookies == null) {
					cookies = new ArrayList<>();
				}
			}
		}
		return cookies.add(cookie);
	}
	
	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body, HttpRequestPacket httpRequestPacket) {
		this.body = body;
		if (body != null) {
			gzip(httpRequestPacket);
		}
	}
	
	private void gzip(HttpRequestPacket httpRequestPacket) {
		//Accept-Encoding
		//检查浏览器是否支持gzip
		String Accept_Encoding = httpRequestPacket.getHeaders().get(HttpConst.RequestHeaderKey.Accept_Encoding);
		if (StringUtils.isNoneBlank(Accept_Encoding)) {
			String[] ss = StringUtils.split(Accept_Encoding, ",");
			if (ArrayUtil.contains(ss, "gzip")) {
				byte[] bs = this.getBody();
				if (bs.length >= 600) {
					byte[] bs2 = ZipUtil.gzip(bs);
					if (bs2.length < bs.length) {
						this.body = bs2;
						this.addHeader(HttpConst.ResponseHeaderKey.Content_Encoding, "gzip");
					}
				}
			}
		}
	}

	/**
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return the httpRequestPacket
	 */
	public HttpRequestPacket getHttpRequestPacket() {
		return httpRequestPacket;
	}

	/**
	 * @param httpRequestPacket the httpRequestPacket to set
	 */
	public void setHttpRequestPacket(HttpRequestPacket httpRequestPacket) {
		this.httpRequestPacket = httpRequestPacket;
	}
	
	@Override
	public String logstr() {
		String str = null;
		if(status instanceof HttpResponseStatus){
			HttpResponseStatus httpResponseStatus = (HttpResponseStatus)status;
			if (httpRequestPacket != null) {
				RequestLine requestLine = httpRequestPacket.getRequestLine();
				if (requestLine != null) {
					str = "\r\n请求：" + requestLine.getInitStr() + "\r\n响应：" + httpResponseStatus.getHeaderText();
				}
			} else {
				str = "\r\n响应：" + httpResponseStatus.getHeaderText();
			}
		}else{
			str = status.getMsg();
		}
		return str;
	}

}
