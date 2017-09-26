/**
 * 
 */
package org.tio.im.server.command.handler.proc;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.packets.Command;
import org.tio.im.common.ws.WsRequestPacket;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.common.ws.WsSessionContext;
import org.tio.im.server.ws.IWsMsgHandler;
import org.tio.im.server.ws.WsMsgHandler;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午4:22:36
 */
public class WsProCmdHandler implements ProCmdHandlerIntf {

	private IWsMsgHandler wsMsgHandler = new WsMsgHandler();
	
	/**
	 * 对httpResponsePacket参数进行补充并返回，如果返回null表示不想和对方建立连接，框架会断开连接，如果返回非null，框架会把这个对象发送给对方
	 * @param httpRequestPacket
	 * @param httpResponsePacket
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		WsRequestPacket wsRequestPacket = (WsRequestPacket) packet;
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
		if (wsRequestPacket.isHandShake()) {
			HttpRequest request = wsSessionContext.getHandshakeRequestPacket();
			HttpResponse httpResponse = wsSessionContext.getHandshakeResponsePacket();
			WsResponsePacket wsResponse = wsMsgHandler.handshake(request, httpResponse, channelContext);
			if (wsResponse == null) {
				Aio.remove(channelContext, "业务层不同意握手");
			}
			return wsResponse;
		}
		return null;
	}

	
	@Override
	public boolean isProtocol(ChannelContext channelContext) throws Exception {
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){
			return false;
		}else if(sessionContext instanceof WsSessionContext){
			return true;
		}
		return false;
	}


	@Override
	public ImPacket chat(ImPacket packet, ChannelContext channelContext) throws Exception {
		WsRequestPacket wsRequestPacket = (WsRequestPacket) packet;
		ImPacket wsResponsePacket = wsMsgHandler.handler(wsRequestPacket, channelContext);
		return wsResponsePacket;
	}


	@Override
	public ImPacket heartbeat(ImPacket packet, ChannelContext channelContext) throws Exception {
		 
		return new ImPacket(Command.COMMAND_HEARTBEAT_REQ, packet.getBody());
	}

}
