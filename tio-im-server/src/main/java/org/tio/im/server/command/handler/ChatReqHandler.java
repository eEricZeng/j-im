package org.tio.im.server.command.handler;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.ImBsHandlerIntf;

/**
 * 
 * 
 * @author tanyaowu 
 *
 */
public class ChatReqHandler implements ImBsHandlerIntf {
	//private static Logger log = LoggerFactory.getLogger(ChatReqHandler.class);

	private static final String CHAT_COMMAND_PREFIX = "c:"; //聊天命令前缀

	public static class ChatCommand {
		/**
		 * 显示一张美女图片的命令
		 */
		public static final String SHOW_MM_IMG = CHAT_COMMAND_PREFIX + "mm";

		/**
		 * 批量查看美女图片的命令
		 */
		public static final String SHOW_BATCH_MM_IMG = CHAT_COMMAND_PREFIX + "mm-10";
		/**
		 * 显示一张风景图片的命令
		 */
		public static final String SHOW_FJ_IMG = CHAT_COMMAND_PREFIX + "fj";

		/**
		 * 显示tio码云地址的命令
		 */
		public static final String SHOW_TIO_IN_MAYUN_IMG = CHAT_COMMAND_PREFIX + "tio";

		/**
		 * 五方会议的命令
		 */
		public static final String SHOW_WFHT_IMG = "五方会谈";

		/**
		 * 来电话了的命令
		 */
		public static final String SHOW_LDHL_IMG = "来电话了";
	}

	/*
	 * 敏感词的替换词
	 */
	public static final String replaceText = "<span style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 4px;'><a href='http://www.gov.cn' target='_blank'>此处为敏感词</a></span>";

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}
		/*ChatReqBody chatReqBody = null;
		try{
			chatReqBody = JSONObject.parseObject(packet.getBody(),ChatReqBody.class);
		}catch(Exception e){
			ImPacket imPacket = null;
			boolean isWebSocket = channelContext.getSessionContext().isWebsocket();
			if(isWebSocket){
				imPacket = new WebSocketPacket(packet.getBody());
			}else{
				imPacket = new ImPacket(packet.getBody());
			}
			Aio.send(channelContext, imPacket);
			return null;
		}
		
		if (chatReqBody != null) {
			String toId = chatReqBody.getTo();
			String text = chatReqBody.getContent();
			if (StringUtils.isBlank(text)) {
				return null;
			}
			Client toClient = null;
			ChannelContext toChannelContext = (ChannelContext) Aio.getChannelContextById(channelContext.getGroupContext(), toId);
			if (toChannelContext != null) {
				toClient = toChannelContext.getSessionContext().getClient();
			}
			String toGroup = chatReqBody.getGroup();
			
			chatReqBody.setCreateTime(SystemTimer.currentTimeMillis());//更新创建时间
			
			byte[] bodyByte = JSONObject.toJSONBytes(chatReqBody);
			
			ImPacket respPacket = null;
			if(packet instanceof HttpRequestPacket){
				respPacket = new HttpResponsePacket((HttpRequestPacket)packet);
			}else{
				respPacket = new ImPacket();
			}
			respPacket.setCommand(Command.COMMAND_CHAT_RESP);
			respPacket.setBody(bodyByte);
			//公聊则发往群里
			if (chatReqBody.getChatType() != null && ChatType.CHAT_TYPE_PUBLIC_VALUE == chatReqBody.getChatType()) {
				Aio.sendToGroup(channelContext.getGroupContext(), toGroup, respPacket);
			} else{
				if (toClient != null) {
					boolean isSuccess = Aio.sendToId(channelContext.getGroupContext(), toId + "", respPacket,true);
					if(isSuccess){
						respPacket.setBody(JSONObject.toJSONBytes(new ChatRespBody().setErrorCode(ImStatus.C1.getCode()).setErrorMsg(ImStatus.C1.getDescription())));
					}else{
						respPacket.setBody(JSONObject.toJSONBytes(new ChatRespBody().setErrorCode(ImStatus.C2.getCode()).setErrorMsg(ImStatus.C2.getDescription())));
					}
					Aio.send(channelContext, respPacket);
				} else {
					respPacket.setBody(JSONObject.toJSONBytes(new ChatRespBody().setErrorCode(ImStatus.C0.getCode()).setErrorMsg(ImStatus.C0.getDescription())));
					Aio.send(channelContext, respPacket);
					log.info(ImStatus.C0.getMsg());
				}
			}
			
		}*/
		return null;
	}
	@Override
	public Command command() {
		return Command.COMMAND_CHAT_REQ;
	}
}
