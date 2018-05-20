/**
 * 
 */
package org.jim.common.tcp;

import org.jim.common.ImPacket;
import org.jim.common.packets.Command;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.tio.core.ChannelContext;

/**
 * TCP协议消息转化包
 * @author WChao
 *
 */
public class TcpConvertPacket implements IConvertProtocolPacket {

	/**
	 * 转TCP协议响应包;
	 */
	@Override
	public ImPacket RespPacket(byte[] body, Command command,ChannelContext channelContext) {
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext instanceof TcpSessionContext){//转TCP协议响应包;
			TcpPacket tcpPacket = new TcpPacket(command,body);
			TcpServerEncoder.encode(tcpPacket, channelContext.getGroupContext(), channelContext);
			tcpPacket.setCommand(command);
			return tcpPacket;
		}
		return null;
	}
	/**
	 * 转TCP协议请求包;
	 */
	@Override
	public ImPacket ReqPacket(byte[] body, Command command,ChannelContext channelContext) {
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext instanceof TcpSessionContext){//转TCP协议请求包;
			TcpPacket tcpPacket = new TcpPacket(command,body);
			TcpServerEncoder.encode(tcpPacket, channelContext.getGroupContext(), channelContext);
			tcpPacket.setCommand(command);
			return tcpPacket;
		}
		return null;
	}

}
