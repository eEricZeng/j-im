package org.tio.im.common.http.websocket;

import org.tio.im.common.http.HttpRequestPacket;

/**
 * 参考了baseio: https://git.oschina.net/generallycloud/baseio
 * 感谢开源作者
 * com.generallycloud.nio.codec.http11.future.WebSocketReadFutureImpl
 * @author tanyaowu 
 *
 */
public class WebSocketRequestPacket extends WebSocketPacket {
	
	private static final long serialVersionUID = -5902050701547331782L;
	
	private HttpRequestPacket httpRequestPacket;

	public WebSocketRequestPacket(){
		
	}
	public WebSocketRequestPacket(HttpRequestPacket httpRequestPacket){
		this.httpRequestPacket = httpRequestPacket;
	}
	public HttpRequestPacket getHttpRequestPacket() {
		return httpRequestPacket;
	}

	public void setHttpRequestPacket(HttpRequestPacket httpRequestPacket) {
		this.httpRequestPacket = httpRequestPacket;
	}
	
}
