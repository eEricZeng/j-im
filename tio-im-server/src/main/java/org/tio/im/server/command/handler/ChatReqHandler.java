package org.tio.im.server.command.handler;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.CmdHandler;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 
 * @author tanyaowu 
 *
 */
public class ChatReqHandler extends CmdHandler {

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}
		ProCmdHandlerIntf imHandler = cmdManager.getProCmdHandler(channelContext);
		return imHandler.chat(packet, channelContext);
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
				RespBody chatRespBody = new RespBody().setCode(ImStatus.C0.getCode()).setCommand(Command.COMMAND_CHAT_RESP).setMsg(ImStatus.C0.getText());
				ImPacket respPacket = Resps.convertPacket(chatRespBody, channelContext);
				respPacket.setStatus(ImStatus.C0);
				return respPacket;
			}else{
				RespBody chatRespBody = new RespBody().setCommand(Command.COMMAND_CHAT_RESP).setMsg(JSONObject.toJSONString(chatBody));
				ImPacket respPacket = Resps.convertPacket(chatRespBody, toChannelContext);
				respPacket.setStatus(ImStatus.C1);
				return respPacket;
			}
		}else{
			RespBody chatRespBody = new RespBody().setCode(ImStatus.C2.getCode()).setCommand(Command.COMMAND_CHAT_RESP).setMsg(ImStatus.C2.getText());
			ImPacket respPacket = Resps.convertPacket(chatRespBody, channelContext);
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
				chatReqBody.setFrom(channelContext.getId());
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
