package org.jim.server.command.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.http.HttpRequest;
import org.jim.common.packets.Command;
import org.jim.common.ws.WsSessionContext;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.handler.processor.handshake.HandshakeCmdProcessor;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * 版本: [1.0]
 * 功能说明: 心跳cmd命令处理器
 * @author : WChao 创建时间: 2017年9月21日 下午3:33:23
 */
public class HandshakeReqHandler extends AbstractCmdHandler {
	
	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		List<HandshakeCmdProcessor> handshakeProcessors = this.getProcessor(channelContext,HandshakeCmdProcessor.class);
		if(CollectionUtils.isEmpty(handshakeProcessors)){
			Aio.remove(channelContext, "没有对应的握手协议处理器HandshakeProCmd...");
			return null;
		}
		HandshakeCmdProcessor handShakeProCmdHandler = handshakeProcessors.get(0);
		ImPacket handShakePacket = handShakeProCmdHandler.handshake(packet, channelContext);
		if (handShakePacket == null) {
			Aio.remove(channelContext, "业务层不同意握手");
			return null;
		}
		ImAio.send(channelContext, handShakePacket);
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
		HttpRequest request = wsSessionContext.getHandshakeRequestPacket();
		handShakeProCmdHandler.onAfterHandshaked(request, channelContext);
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HANDSHAKE_REQ;
	}
}
