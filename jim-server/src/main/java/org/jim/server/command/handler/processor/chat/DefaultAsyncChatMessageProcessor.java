package org.jim.server.command.handler.processor.chat;

import org.jim.common.packets.ChatBody;
import org.tio.core.ChannelContext;
/**
 * @author WChao
 * @date 2018年4月3日 下午1:12:30
 */
public class DefaultAsyncChatMessageProcessor extends BaseAsyncChatMessageProcessor {
	
	@Override
	public void doHandler(ChatBody chatBody, ChannelContext channelContext){
		System.out.println("走一个呗,"+chatBody.toJsonString());
	}
}
