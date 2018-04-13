package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.CloseReqBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.utils.ImKit;
import org.tio.im.common.utils.JsonKit;
import org.tio.im.server.command.AbCmdHandler;

public class CloseReqHandler extends AbCmdHandler
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(CloseReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		CloseReqBody closeReqBody = null;
		try{
			closeReqBody = JsonKit.toBean(packet.getBody(),CloseReqBody.class);
		}catch (Exception e) {//关闭请求消息格式不正确
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_CLOSE_REQ, ImStatus.C10020), channelContext);
		}
		if(closeReqBody == null || closeReqBody.getUserid() == null){
			ImAio.remove(channelContext, "收到关闭请求");
		}else{
			String userid = closeReqBody.getUserid();
			ImAio.remove(userid, "收到关闭请求!");
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_CLOSE_REQ, ImStatus.C10021), channelContext);
		}
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_CLOSE_REQ;
	}
}
