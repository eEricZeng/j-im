/**
 * 
 */
package org.jim.server.command.handler.processor.handshake;

import org.jim.common.ImPacket;
import org.jim.common.Protocol;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.common.packets.HandshakeBody;
import org.jim.common.packets.RespBody;
import org.jim.common.tcp.TcpSessionContext;
import org.jim.common.utils.ImKit;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午8:11:34
 */
public class TcpHandshakeProcessor implements HandshakeProcessorIntf {

	@Override
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		RespBody handshakeBody = new RespBody(Command.COMMAND_HANDSHAKE_RESP,new HandshakeBody(Protocol.HANDSHAKE_BYTE));
		ImPacket handshakePacket = ImKit.ConvertRespPacket(handshakeBody,channelContext);
		return handshakePacket;
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
