package org.tio.im.server.command;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;
/**
 * 
 * @author tanyaowu 
 *
 */
public interface ImBsHandlerIntf
{
	/**
	 * 
		 * 功能描述：[命令主键]
		 * 创建者：WChao 创建时间: 2017年7月17日 下午2:31:51
		 * @return
		 *
	 */
	public Command command();
	/**
	 * 
	 * @param packet
	 * @param channelContext
	 * @return
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 下午1:08:45
	 *
	 */
	public Object handler(ImPacket packet, ChannelContext channelContext)  throws Exception;
}
