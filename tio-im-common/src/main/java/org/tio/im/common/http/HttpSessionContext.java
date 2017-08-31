package org.tio.im.common.http;

import org.tio.im.common.SessionContext;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpSessionContext extends SessionContext
{
	
	/**
	 * websocket 握手请求包
	 */
	protected HttpRequestPacket handshakeRequestPacket = null;
	
	/**
	 * websocket 握手响应包
	 */
	protected HttpResponsePacket handshakeResponsePacket = null;
	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public HttpSessionContext()
	{
		
	}
	public HttpRequestPacket getHandshakeRequestPacket() {
		return handshakeRequestPacket;
	}
	public void setHandshakeRequestPacket(HttpRequestPacket handshakeRequestPacket) {
		this.handshakeRequestPacket = handshakeRequestPacket;
	}
	public HttpResponsePacket getHandshakeResponsePacket() {
		return handshakeResponsePacket;
	}
	public void setHandshakeResponsePacket(HttpResponsePacket handshakeResponsePacket) {
		this.handshakeResponsePacket = handshakeResponsePacket;
	}

}
