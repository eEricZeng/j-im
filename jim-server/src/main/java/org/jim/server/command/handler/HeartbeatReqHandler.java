package org.jim.server.command.handler;

import org.jim.common.ImPacket;
import org.jim.common.Protocol;
import org.jim.common.packets.Command;
import org.jim.common.packets.HeartbeatBody;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ImKit;
import org.jim.server.command.AbstractCmdHandler;
import org.tio.core.ChannelContext;

/**
 *
 */
public class HeartbeatReqHandler extends AbstractCmdHandler
{
	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		RespBody heartbeatBody = new RespBody(Command.COMMAND_HEARTBEAT_REQ).setData(new HeartbeatBody(Protocol.HEARTBEAT_BYTE));
		ImPacket heartbeatPacket = ImKit.ConvertRespPacket(heartbeatBody,channelContext);
		return heartbeatPacket;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HEARTBEAT_REQ;
	}
}
