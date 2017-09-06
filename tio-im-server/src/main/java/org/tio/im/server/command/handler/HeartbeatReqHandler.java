package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.ImBsHandlerIntf;

public class HeartbeatReqHandler implements ImBsHandlerIntf
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HeartbeatReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HEARTBEAT_REQ;
	}
}
