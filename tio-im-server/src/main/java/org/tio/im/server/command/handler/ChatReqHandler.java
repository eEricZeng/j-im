package org.tio.im.server.command.handler;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
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
		imHandler.chat(packet, channelContext);
		return null;
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
