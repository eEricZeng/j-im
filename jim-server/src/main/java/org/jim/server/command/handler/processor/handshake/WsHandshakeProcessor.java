/**
 * 
 */
package org.jim.server.command.handler.processor.handshake;

import org.jim.common.ImPacket;
import org.jim.common.Protocol;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.common.ws.WsRequestPacket;
import org.jim.common.ws.WsResponsePacket;
import org.jim.common.ws.WsSessionContext;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午4:22:36
 */
public class WsHandshakeProcessor implements HandshakeProcessorIntf {

	/**
	 * 对httpResponsePacket参数进行补充并返回，如果返回null表示不想和对方建立连接，框架会断开连接，如果返回非null，框架会把这个对象发送给对方
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: Wchao
	 */
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		WsRequestPacket wsRequestPacket = (WsRequestPacket) packet;
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
		if (wsRequestPacket.isHandShake()) {
			WsResponsePacket wsResponsePacket = new WsResponsePacket();
			wsResponsePacket.setHandShake(true);
			wsResponsePacket.setCommand(Command.COMMAND_HANDSHAKE_RESP);
			wsSessionContext.setHandshaked(true);
			return wsResponsePacket;
		}
		return null;
	}
	/**
	 * 握手成功后
	 * @param packet
	 * @param channelContext
	 * @throws Exception
	 * @author Wchao
	 */
	@Override
	public void onAfterHandshaked(ImPacket packet, ChannelContext channelContext)throws Exception {
		
	}
	
	@Override
	public boolean isProtocol(ChannelContext channelContext){
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){
			return false;
		}else if(sessionContext instanceof WsSessionContext){
			return true;
		}
		return false;
	}


	@Override
	public String name() {
		
		return Protocol.WEBSOCKET;
	}

}
