package org.tio.im.common;

import org.tio.im.common.packets.Client;
import org.tio.monitor.RateLimiterWrap;

/**
 * 
 * @author tanyaowu 
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
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public ImSessionContext()
	{
		
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public static void main(String[] args)
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
}
