package org.tio.im.common.http.websocket;

import java.util.List;

import org.tio.im.common.http.HttpSessionContext;
/**
 * 
 * @author tanyaowu 
 *
 */
public class WebSocketSessionContext extends HttpSessionContext
{
	
	/**
	 * 是否已经握过手
	 */
	private boolean isHandshaked = false;
	
	/**
	 * 是否是走了websocket协议
	 */
	private boolean isWebsocket = false;
	
	//websocket 协议用到的，有时候数据包是分几个到的，注意那个fin字段，本im暂时不支持
	private List<byte[]> lastParts = null;
	
	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public WebSocketSessionContext()
	{
		
	}

	public boolean isHandshaked() {
		return isHandshaked;
	}

	public void setHandshaked(boolean isHandshaked) {
		this.isHandshaked = isHandshaked;
	}

	public boolean isWebsocket() {
		return isWebsocket;
	}

	public void setWebsocket(boolean isWebsocket) {
		this.isWebsocket = isWebsocket;
	}

	public List<byte[]> getLastParts() {
		return lastParts;
	}

	public void setLastParts(List<byte[]> lastParts) {
		this.lastParts = lastParts;
	}
}
