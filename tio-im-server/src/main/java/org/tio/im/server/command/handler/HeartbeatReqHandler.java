package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.HeartbeatBody;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.CmdHandler;

public class HeartbeatReqHandler extends CmdHandler
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HeartbeatReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		RespBody heartbeatBody = new RespBody(Command.COMMAND_HEARTBEAT_REQ).setData(new HeartbeatBody().setHbbyte(Protocol.HEARTBEAT_BYTE));
		ImPacket heartbeatPacket = Resps.convertRespPacket(heartbeatBody.toByte(),Command.COMMAND_HEARTBEAT_REQ,channelContext);
		return heartbeatPacket;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HEARTBEAT_REQ;
	}
}
