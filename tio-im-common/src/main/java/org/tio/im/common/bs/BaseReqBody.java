/**
 * 
 */
package org.tio.im.common.bs;

import org.tio.utils.SystemTimer;

/**
 * 
 * 版本: [1.0]
 * 功能说明: 基础请求体
 * 作者: WChao 创建时间: 2017年7月13日 下午6:06:33
 */
public class BaseReqBody
{
	
	private Long time;
	/**
	 * 
	 */
	public BaseReqBody()
	{
		time = SystemTimer.currentTimeMillis();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{}

	public Long getTime()
	{
		return time;
	}

	public void setTime(Long time)
	{
		this.time = time;
	}
}


