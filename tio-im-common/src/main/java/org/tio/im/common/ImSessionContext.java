package org.tio.im.common;

import org.tio.im.common.packets.Client;
import org.tio.monitor.RateLimiterWrap;
import org.tio.server.intf.ServerAioHandler;

/**
 * 
 * @author wchao 
 *
 */
public class ImSessionContext extends SessionContext
{
	/**
	 * 消息请求频率控制器
	 */
	private RateLimiterWrap requestRateLimiter = null;
	
	private Client client = null;
	
	private String token = null;
	/**
	 * 通道所属协议处理器;
	 */
	private ServerAioHandler serverHandler;
	
	/**
	 * 
	 *
	 * @author: wchao
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public ImSessionContext()
	{
		
	}
	/**
	 * @return the client
	 */
	public Client getClient()
	{
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client)
	{
		this.client = client;
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token)
	{
		this.token = token;
	}

	/**
	 * @return the requestRateLimiter
	 */
	public RateLimiterWrap getRequestRateLimiter() {
		return requestRateLimiter;
	}

	/**
	 * @param requestRateLimiter the requestRateLimiter to set
	 */
	public void setRequestRateLimiter(RateLimiterWrap requestRateLimiter) {
		this.requestRateLimiter = requestRateLimiter;
	}

	public ServerAioHandler getServerHandler() {
		return serverHandler;
	}

	public ImSessionContext setServerHandler(ServerAioHandler serverHandler) {
		this.serverHandler = serverHandler;
		return this;
	}
}
