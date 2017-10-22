package org.tio.im.server.command.handler.proc;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午1:23:03
 */
public interface ProCmdHandlerIntf
{
	public boolean isProtocol(ChannelContext channelContext)throws Exception;
	public ImPacket handshake(ImPacket packet,ChannelContext channelContext)  throws Exception;
	public ImPacket chat(ImPacket packet,ChannelContext channelContext) throws Exception;
}
