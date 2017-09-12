package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.CmdHandler;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;

public class HandshakeReqHandler extends CmdHandler {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HandshakeReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		ProCmdHandlerIntf imHandler = cmdManager.getProCmdHandler(channelContext);
		
		return imHandler.handshake(packet, channelContext);
	}

	@Override
	public Command command() {
		return Command.COMMAND_HANDSHAKE_REQ;
	}
}
