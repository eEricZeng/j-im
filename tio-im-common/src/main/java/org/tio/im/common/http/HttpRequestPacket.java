package org.tio.im.common.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.basjes.parse.useragent.UserAgent;
/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpRequestPacket extends HttpPacket {
	
	
	private static final long serialVersionUID = -1943038287153219073L;


	private static Logger log = LoggerFactory.getLogger(HttpRequestPacket.class);

	
	private RequestLine requestLine = null;
	/**
	 * 请求参数
	 */
	private Map<String, Object[]> params = null;
	private List<Cookie> cookies = null;
	private Map<String, Cookie> cookieMap = null;
	private int contentLength;
	private String bodyString;
	private RequestBodyFormat bodyFormat;
	private String charset = HttpConst.CHARSET_NAME;
	
	private UserAgent userAgent;
	
	public static final int MAX_HEADER_LENGTH = 20480;//请求头最大支持多少字节;

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public HttpRequestPacket() {
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

	public Cookie getCookieByName(String cooiename){
		if (cookieMap == null) {
			return null;
		}
		return cookieMap.get(cooiename);
	}
	
	/**
	 * @return the firstLine
	 */
	public RequestLine getRequestLine() {
		return requestLine;
	}

	/**
	 * @param requestLine the requestLine to set
	 */
	public void setRequestLine(RequestLine requestLine) {
		this.requestLine = requestLine;
	}

	/**
	 * 设置好header后，会把cookie等头部信息也设置好
	 * @param headers the headers to set
	 * @param channelContext 
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
		if (headers != null) {
			parseCookie();
		}
		
//		String Sec_WebSocket_Key = headers.get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key);
//		if (StringUtils.isNoneBlank(Sec_WebSocket_Key)) {
//			ImSessionContext httpSessionContext = channelContext.getSessionContext();
//			httpSessionContext.setWebsocket(true);
//		}
	}

	public void parseCookie() {
		String cookieline = headers.get(HttpConst.RequestHeaderKey.Cookie);
		if (StringUtils.isNotBlank(cookieline)) {
			cookies = new ArrayList<>();
			cookieMap = new HashMap<>();
			Map<String, String> _cookiemap = Cookie.getEqualMap(cookieline);
			List<Map<String, String>> cookieListMap = new ArrayList<Map<String, String>>();
			for (Entry<String, String> cookieMapEntry : _cookiemap.entrySet()) {
				HashMap<String, String> cookieOneMap = new HashMap<String, String>();
				cookieOneMap.put(cookieMapEntry.getKey(), cookieMapEntry.getValue());
				cookieListMap.add(cookieOneMap);

				Cookie cookie = Cookie.buildCookie(cookieOneMap);
				cookies.add(cookie);
				cookieMap.put(cookie.getName(), cookie);
				log.error("收到cookie:{}", cookie.toString());
			}
		}
	}

	/**
	 * @return the bodyLength
	 */
	public int getContentLength() {
		return contentLength;
	}
	
	/**
	 * @param bodyLength the bodyLength to set
	 */
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
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
	 * @return the cookieMap
	 */
	public Map<String, Cookie> getCookieMap() {
		return cookieMap;
	}

	/**
	 * @param cookieMap the cookieMap to set
	 */
	public void setCookieMap(Map<String, Cookie> cookieMap) {
		this.cookieMap = cookieMap;
	}

	/**
	 * @return the bodyString
	 */
	public String getBodyString() {
		return bodyString;
	}

	/**
	 * @param bodyString the bodyString to set
	 */
	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	/**
	 * @return the params
	 */
	public Map<String, Object[]> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, Object[]> params) {
		this.params = params;
	}

	/**
	 * @return the bodyFormat
	 */
	public RequestBodyFormat getBodyFormat() {
		return bodyFormat;
	}

	/**
	 * @param bodyFormat the bodyFormat to set
	 */
	public void setBodyFormat(RequestBodyFormat bodyFormat) {
		this.bodyFormat = bodyFormat;
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
	
	
	public UserAgent getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(UserAgent userAgent) {
		this.userAgent = userAgent;
	}

	/** 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public String logstr() {
		if (requestLine != null) {
			return requestLine.getInitStr();
		}
		return "";
	}

}
