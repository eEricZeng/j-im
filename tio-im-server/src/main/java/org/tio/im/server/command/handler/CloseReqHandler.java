package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.CmdHandler;

public class CloseReqHandler extends CmdHandler
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(CloseReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		Aio.remove(channelContext, "收到关闭请求");
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_CLOSE_REQ;
	}
}
