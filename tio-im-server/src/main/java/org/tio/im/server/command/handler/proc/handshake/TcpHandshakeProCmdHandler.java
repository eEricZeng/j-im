/**
 * 
 */
package org.tio.im.server.command.handler.proc.handshake;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.HandshakeBody;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.tcp.TcpSessionContext;
import org.tio.im.common.utils.Resps;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午8:11:34
 */
public class TcpHandshakeProCmdHandler implements HandshakeProCmdIntf {

	@Override
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		RespBody handshakeBody = new RespBody(Command.COMMAND_HANDSHAKE_RESP).setData(new HandshakeBody().setHbyte(Protocol.HANDSHAKE_BYTE));
		ImPacket handshakePacket = Resps.convertRespPacket(handshakeBody.toByte(), Command.COMMAND_HANDSHAKE_RESP, channelContext);
		return handshakePacket;
	}

	
	@Override
	public boolean isProtocol(ChannelContext channelContext){
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){
			return false;
		}else if(sessionContext instanceof TcpSessionContext){
			return true;
		}
		return false;
	}


	@Override
	public String name() {
		
		return Protocol.TCP;
	}
	
}
