package org.tio.im.server.command.handler;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.ChatType;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.CmdHandler;

import com.alibaba.fastjson.JSONObject;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月22日 下午2:58:59
 */
public class ChatReqHandler extends CmdHandler {

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		/*ProCmdHandlerIntf imHandler = cmdManager.getProCmdHandler(channelContext);
		return imHandler.chat(packet, channelContext);*/
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}
		ChatBody chatBody = ChatReqHandler.parseChatBody(packet.getBody(), channelContext);
		if(chatBody == null){
			ImPacket respChatPacket = ChatReqHandler.convertChatResPacket(chatBody, channelContext);
			return respChatPacket;
		}
		if(chatBody.getChatType() == null || ChatType.CHAT_TYPE_PRIVATE.getNumber() == chatBody.getChatType()){
			ImPacket respChatPacket = ChatReqHandler.convertChatResPacket(chatBody, channelContext);
			ChannelContext toChannleContext = ChatReqHandler.getToChannel(chatBody, channelContext.getGroupContext());
			if(toChannleContext != null){
				Aio.send(toChannleContext, respChatPacket);
				RespBody chatStatusPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C1);
				return Resps.convertRespPacket(chatStatusPacket, channelContext);
			}else{
				return respChatPacket;
			}
		}else if(ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()){
			String group_id = chatBody.getGroup_id();
			ImPacket imPacket = new ImPacket(Command.COMMAND_CHAT_RESP,JSONObject.toJSONBytes(new RespBody(Command.COMMAND_CHAT_RESP).setData(chatBody.toString())));
			ImAio.sendToGroup(channelContext.getGroupContext(), group_id, imPacket);
			RespBody chatStatusPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C1);
			return Resps.convertRespPacket(chatStatusPacket, channelContext);
		}
		return null;
	}
	/**
	 * 功能描述：[转换聊天请求不同协议响应包]
	 * 创建者：WChao 创建时间: 2017年8月29日 下午7:22:53
	 * @param packet 聊天请求包;
	 * @param channelContext;来源channel;
	 * @return
	 * @throws Exception 
	 *
 */
	public static ImPacket convertChatResPacket(ImPacket imPacket, ChannelContext channelContext) throws Exception{
		ChatBody chatBody = parseChatBody(imPacket.getBody(), channelContext);
		return convertChatResPacket(chatBody,channelContext);
	}
	/**
		 * 功能描述：[转换聊天请求不同协议响应包]
		 * 创建者：WChao 创建时间: 2017年8月29日 下午7:22:53
		 * @param chatBody 聊天消息体;
		 * @param channelContext;来源channel;
		 * @return
		 * @throws Exception 
		 *
	 */
	public static ImPacket convertChatResPacket(ChatBody chatBody, ChannelContext channelContext) throws Exception{
		if(chatBody != null){
			ChannelContext toChannelContext = getToChannel(chatBody, channelContext.getGroupContext());
			if(toChannelContext == null){
				RespBody chatRespBody = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C0);
				ImPacket respPacket = Resps.convertRespPacket(chatRespBody, channelContext);
				respPacket.setStatus(ImStatus.C0);
				return respPacket;
			}else{
				RespBody chatRespBody = new RespBody(Command.COMMAND_CHAT_RESP).setData(JSONObject.toJSONString(chatBody));
				ImPacket respPacket = Resps.convertRespPacket(chatRespBody, toChannelContext);
				respPacket.setStatus(ImStatus.C1);
				return respPacket;
			}
		}else{
			RespBody chatRespBody = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C2);
			ImPacket respPacket = Resps.convertRespPacket(chatRespBody, channelContext);
			respPacket.setStatus(ImStatus.C2);
			return respPacket;
		}
	}
	
	public static ChannelContext getToChannel(ChatBody chatBody,GroupContext groupContext){
		if(chatBody == null){
			return null;
		}
		return Aio.getChannelContextByUserid(groupContext,chatBody.getTo());
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	public static ChatBody parseChatBody(byte[] body){
		if(body == null)
			return null;
		ChatBody chatReqBody = null;
		try{
			String text = new String(body,HttpConst.CHARSET_NAME);
		    chatReqBody = JSONObject.parseObject(text,ChatBody.class);
			if(chatReqBody != null){
				if(chatReqBody.getCreateTime() == null || "".equals(chatReqBody.getCreateTime()))
					chatReqBody.setCreateTime(System.currentTimeMillis());
				chatReqBody.setId(UUIDSessionIdGenerator.instance.sessionId(null));
				return chatReqBody;
			}
		}catch(Exception e){
			
		}
		return chatReqBody;
	}
	/**
	 * 转换为聊天消息结构;
	 * @param body
	 * @param channelContext
	 * @return
	 */
	public static ChatBody parseChatBody(byte[] body,ChannelContext channelContext){
		ChatBody chatReqBody = parseChatBody(body);
		if(chatReqBody != null){
			if(chatReqBody.getFrom() == null || "".equals(chatReqBody.getFrom())){
				ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
				User user = imSessionContext.getClient().getUser();
				if(user != null){
					chatReqBody.setFrom(user.getNick());
				}else{
					chatReqBody.setFrom(channelContext.getId());
				}
			}
		}
		return chatReqBody;
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	public static ChatBody parseChatBody(String bodyStr){
		if(bodyStr == null)
			return null;
		try {
			return parseChatBody(bodyStr.getBytes(HttpConst.CHARSET_NAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 格式化状态码消息响应体;
	 * @param status
	 * @return
	 */
	public static byte[] toImStatusBody(ImStatus status){
		return JSONObject.toJSONBytes(new RespBody().setCode(status.getCode()).setMsg(status.getDescription()+" "+status.getText()));
	}
	@Override
	public Command command() {
		return Command.COMMAND_CHAT_REQ;
	}
}
