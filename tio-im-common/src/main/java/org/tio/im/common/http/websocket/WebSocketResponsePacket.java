package org.tio.im.common.http.websocket;

import org.tio.im.common.http.HttpResponsePacket;

/**
 * 参考了baseio: https://git.oschina.net/generallycloud/baseio
 * 感谢开源作者
 * com.generallycloud.nio.codec.http11.future.WebSocketReadFutureImpl
 * @author tanyaowu 
 *
 */
public class WebSocketResponsePacket extends WebSocketPacket {
	
	private static final long serialVersionUID = -6623962818214861422L;
	
	private HttpResponsePacket httpResponsePacket;
	
	public WebSocketResponsePacket(){
		
	}
	public WebSocketResponsePacket(HttpResponsePacket httpResponsePacket){
		this.httpResponsePacket = httpResponsePacket;
	}
	public HttpResponsePacket getHttpResponsePacket() {
		return httpResponsePacket;
	}

	public void setHttpResponsePacket(HttpResponsePacket httpResponsePacket) {
		this.httpResponsePacket = httpResponsePacket;
	}
}
