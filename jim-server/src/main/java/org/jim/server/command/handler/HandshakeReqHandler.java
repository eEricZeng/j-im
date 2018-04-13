package org.jim.server.command.handler;

import org.jim.common.ImPacket;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.server.command.AbCmdHandler;
import org.jim.server.command.handler.processor.ProcessorIntf;
import org.jim.server.command.handler.processor.handshake.HandshakeProcessorIntf;

public class HandshakeReqHandler extends AbCmdHandler {
	
	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		ProcessorIntf proCmdHandler = this.getProcessor(channelContext);
		if(proCmdHandler == null){
			Aio.remove(channelContext, "没有对应的握手协议处理器HandshakeProCmd...");
			return null;
		}
		HandshakeProcessorIntf handShakeProCmdHandler = (HandshakeProcessorIntf)proCmdHandler;
		ImPacket handShakePacket = handShakeProCmdHandler.handshake(packet, channelContext);
		if (handShakePacket == null) {
			Aio.remove(channelContext, "业务层不同意握手");
		}
		return handShakePacket;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HANDSHAKE_REQ;
	}
}
