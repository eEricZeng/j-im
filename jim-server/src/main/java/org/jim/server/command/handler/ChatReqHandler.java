package org.jim.server.command.handler;

import org.jim.common.Const;
import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.ChatType;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ChatKit;
import org.jim.server.command.AbCmdHandler;
import org.jim.server.command.handler.processor.chat.MsgQueueRunnable;
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
		ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), channelContext);
		//聊天数据格式不正确
		if(chatBody == null || chatBody.getChatType() == null){
			ImPacket respChatPacket = ChatKit.dataInCorrectRespPacket(channelContext);
			return respChatPacket;
		}
		//异步调用业务处理消息接口
		if(ChatType.forNumber(chatBody.getChatType()) != null){
			packet.setBody(chatBody.toByte());
			MsgQueueRunnable msgQueueRunnable = (MsgQueueRunnable)channelContext.getAttribute(Const.CHAT_QUEUE);
			msgQueueRunnable.addMsg(packet);
			msgQueueRunnable.getExecutor().execute(msgQueueRunnable);
		}
		ImPacket chatPacket = new ImPacket(Command.COMMAND_CHAT_REQ,new RespBody(Command.COMMAND_CHAT_REQ,chatBody).toByte());
		//设置同步序列号;
		chatPacket.setSynSeq(packet.getSynSeq());
		//私聊
		if(ChatType.CHAT_TYPE_PRIVATE.getNumber() == chatBody.getChatType()){
			String toId = chatBody.getTo();
			if(ChatKit.isOnline(toId,imConfig)){
				ImAio.sendToUser(toId, chatPacket);
				//发送成功响应包
				return ChatKit.sendSuccessRespPacket(channelContext);
			}else{
				//用户不在线响应包
				return ChatKit.offlineRespPacket(channelContext);
			}
			//群聊
		}else if(ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()){
			String group_id = chatBody.getGroup_id();
			ImAio.sendToGroup(group_id, chatPacket);
			//发送成功响应包
			return ChatKit.sendSuccessRespPacket(channelContext);
		}
		return null;
	}
	@Override
	public Command command() {
		return Command.COMMAND_CHAT_REQ;
	}
}
