package org.jim.server.command.handler;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.jim.common.ImAio;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.ChatType;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ChatKit;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.handler.processor.chat.ChatCmdProcessor;
import org.jim.server.command.handler.processor.chat.MsgQueueRunnable;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * 版本: [1.0]
 * 功能说明: 聊天请求cmd消息命令处理器
 * @author : WChao 创建时间: 2017年9月22日 下午2:58:59
 */
public class ChatReqHandler extends AbstractCmdHandler {

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}
		ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), channelContext);
		packet.setBody(chatBody.toByte());
		//聊天数据格式不正确
		if(chatBody == null || chatBody.getChatType() == null){
			ImPacket respChatPacket = ChatKit.dataInCorrectRespPacket(channelContext);
			return respChatPacket;
		}
		List<ChatCmdProcessor> chatProcessors = this.getProcessorAndNotEqualName(Sets.newHashSet(ImConst.BASE_ASYNC_CHAT_MESSAGE_PROCESSOR),ChatCmdProcessor.class);
		if(CollectionUtils.isNotEmpty(chatProcessors)){
			chatProcessors.forEach(chatProcessor -> chatProcessor.handler(packet,channelContext));
		}
		//异步调用业务处理消息接口
		if(ChatType.forNumber(chatBody.getChatType()) != null){
			MsgQueueRunnable msgQueueRunnable = (MsgQueueRunnable)channelContext.getAttribute(ImConst.CHAT_QUEUE);
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
