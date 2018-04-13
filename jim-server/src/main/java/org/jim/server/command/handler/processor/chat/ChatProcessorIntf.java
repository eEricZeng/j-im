package org.jim.server.command.handler.processor.chat;

import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;
import org.jim.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 * @date 2018年4月2日 下午3:21:01
 */
public interface ChatProcessorIntf extends ProcessorIntf{
	public void handler(ImPacket chatPacket,ChannelContext channelContext)  throws Exception;
}
