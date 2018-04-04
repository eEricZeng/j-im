package org.tio.im.server.command.handler.processor.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.utils.ChatKit;

/**
 * @author WChao
 * @date 2018年4月3日 下午1:12:30
 */
public class DefaultChatProcessor extends AbstractChatProcessor{
	
	Logger log = LoggerFactory.getLogger(DefaultChatProcessor.class);
	
	@Override
	public void handler(ImPacket chatPacket, ChannelContext channelContext) throws Exception {
		log.info("存储聊天消息....");
		ChatBody chatBody = ChatKit.toChatBody(chatPacket.getBody(), channelContext);
		boolean isOnline = ChatKit.isOnline(chatBody.getTo());
		if(!isOnline){
			//log.info("存储离线消息....");
			
		}
	}
}
