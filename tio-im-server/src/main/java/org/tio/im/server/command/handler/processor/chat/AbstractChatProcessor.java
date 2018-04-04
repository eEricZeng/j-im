package org.tio.im.server.command.handler.processor.chat;

import org.tio.core.ChannelContext;
/**
 * @author WChao
 * @date 2018年4月3日 下午1:13:32
 */
public abstract class AbstractChatProcessor implements ChatProcessorIntf {
	
	public static final String DEAULT_CHAT = "default_chat";
	
	@Override
	public boolean isProtocol(ChannelContext channelContext) {
		return true;
	}

	@Override
	public String name() {
		return DEAULT_CHAT;
	}
}
