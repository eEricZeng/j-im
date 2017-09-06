package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.ImBsHandlerIntf;


public class HandshakeReqHandler implements ImBsHandlerIntf {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HandshakeReqHandler.class);

	private ImPacket handshakeRespPacket = new ImPacket(Command.COMMAND_HANDSHAKE_RESP);

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		//ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
	/*	imSessionContext.setHandshaked(true);

		boolean isWebsocket = imSessionContext.isWebsocket();
		if (isWebsocket) {
			WebSocketRequestPacket wsRequestPacket = (WebSocketRequestPacket) packet;
			HttpRequestPacket httpRequestPacket = wsRequestPacket.getHttpRequestPacket();
			WebSocketResponsePacket wsResponsePacket = WebSocketRequestDecoder.updateResponsePacketProtocol(httpRequestPacket, channelContext);
			if (wsResponsePacket != null) {
				UserAgent userAgent = httpRequestPacket.getUserAgent();
				if (userAgent != null) {

					String clientnode = StringUtils.rightPad(channelContext.getClientNode().toString(), 22);
					String id = StringUtils.leftPad(channelContext.getId(), 32);
					String formatedUserAgent = ImUtils.formatUserAgent(channelContext);
					userAgentLog.info("{} {} {}", clientnode, id, formatedUserAgent);
					
					Client client = imSessionContext.getClient();
					client.setUseragent(formatedUserAgent);
				}

				wsResponsePacket.setCommand(Command.COMMAND_HANDSHAKE_RESP);
				Aio.send(channelContext, wsResponsePacket);
			} else {
				Aio.remove(channelContext, "不是websocket协议");
			}
		} else {
			Aio.send(channelContext, handshakeRespPacket);
		}*/

		return handshakeRespPacket;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HANDSHAKE_REQ;
	}
}
