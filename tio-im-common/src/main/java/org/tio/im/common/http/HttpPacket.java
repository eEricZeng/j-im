package org.tio.im.common.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.ImPacket;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpPacket extends ImPacket implements HttpConst{

	private static final long serialVersionUID = 5544430463445082373L;

	private static Logger log = LoggerFactory.getLogger(HttpPacket.class);

	/**
	 * 消息体最多为多少
	 */
	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 5.1); //只支持多少M数据

	//不包含cookie的头部
	protected Map<String, String> headers = new HashMap<>();

	public HttpPacket() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.info("");
	}

 	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void removeHeader(String key, String value) {
		headers.remove(key);
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/** 
	 * @see org.tio.core.intf.Packet#logstr()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月22日 下午3:15:18
	 * 
	 */
	@Override
	public String logstr() {
		if(body != null){
			try {
				return new String(body,CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
