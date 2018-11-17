/**
 * 
 */
package org.jim.common.ws;

import org.jim.common.ImPacket;
import org.jim.common.packets.Command;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.tio.core.ChannelContext;

/**
 * Ws协议消息转化包
 * @author WChao
 *
 */
public class WsConvertPacket implements IConvertProtocolPacket {

	/**
	 * WebSocket响应包;
	 */
	@Override
	public ImPacket RespPacket(byte[] body, Command command,ChannelContext channelContext) {
		Object sessionContext = channelContext.getAttribute();
		//转ws协议响应包;
		if(sessionContext instanceof WsSessionContext){
			WsResponsePacket wsResponsePacket = new WsResponsePacket();
			wsResponsePacket.setBody(body);
			wsResponsePacket.setWsOpcode(Opcode.TEXT);
			wsResponsePacket.setCommand(command);
			return wsResponsePacket;
		}
		return null;
	}

	@Override
	public ImPacket ReqPacket(byte[] body, Command command,ChannelContext channelContext) {
		
		return null;
	}
}
