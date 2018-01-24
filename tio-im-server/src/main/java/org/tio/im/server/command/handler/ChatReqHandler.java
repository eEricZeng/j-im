package org.tio.im.server.command.handler;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.ChatType;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.utils.ChatKit;
import org.tio.im.server.command.AbCmdHandler;
import org.tio.utils.lock.SetWithLock;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月22日 下午2:58:59
 */
public class ChatReqHandler extends AbCmdHandler {

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}
		Integer synSeq = 0;
		ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), channelContext);
		if(chatBody == null){//聊天数据格式不正确
			ImPacket respChatPacket = ChatKit.dataInCorrectRespPacket(channelContext);
			return respChatPacket;
		}
		ImPacket chatPacket = new ImPacket(Command.COMMAND_CHAT_REQ,new RespBody(Command.COMMAND_CHAT_REQ,chatBody.toJsonString()).toByte());
		if(packet.getImSynSeq() > 0){//设置同步序列号;
			synSeq = packet.getImSynSeq();
			chatPacket.setSynSeq(synSeq);
		}
		if(chatBody.getChatType() == null || ChatType.CHAT_TYPE_PRIVATE.getNumber() == chatBody.getChatType()){//私聊
			SetWithLock<ChannelContext> toChannleContexts = ImAio.getChannelContextsByUserid(channelContext.getGroupContext(),chatBody.getTo());
			if(toChannleContexts != null && toChannleContexts.size() > 0){
				ImAio.send(toChannleContexts, chatPacket);
				return ChatKit.sendSuccessRespPacket(channelContext);//发送成功响应包
			}else{
				return ChatKit.offlineRespPacket(channelContext);//用户不在线响应包
			}
		}else if(ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()){//群聊
			String group_id = chatBody.getGroup_id();
			ImAio.sendToGroup(channelContext.getGroupContext(), group_id, chatPacket);
			return ChatKit.sendSuccessRespPacket(channelContext);//发送成功响应包
		}
		return null;
	}
	
	@Override
	public Command command() {
		return Command.COMMAND_CHAT_REQ;
	}
}
