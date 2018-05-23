package org.jim.server.command.handler;

import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.packets.CloseReqBody;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbCmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;


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
		}
		return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_CLOSE_REQ, ImStatus.C10021), channelContext);
	}

	@Override
	public Command command() {
		return Command.COMMAND_CLOSE_REQ;
	}
}
