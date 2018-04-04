package org.tio.im.server.command.handler.processor.chat;

import org.tio.core.ChannelContext;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 * @date 2018年4月2日 下午3:21:01
 */
public interface ChatProcessorIntf extends ProcessorIntf{
	public void handler(ChatBody chatBody,ChannelContext channelContext)  throws Exception;
}
