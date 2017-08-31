/**
 * 
 */
package org.tio.im.server.tcp;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImPacketType;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.Command;
import org.tio.im.common.tcp.TcpPacket;
import org.tio.im.common.tcp.TcpRequestDecoder;
import org.tio.im.common.tcp.TcpResponseEncoder;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.im.server.util.Resps;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午7:44:48
 */
public class TcpServerHandler extends AbServerHandler{
	
	Logger logger = Logger.getLogger(TcpServerHandler.class);
	
	@Override
	public void init() {
	}

	@Override
	public boolean isProtocol(ByteBuffer buffer , Packet packet,ChannelContext channelContext){
		ImSessionContext sessionContext = (ImSessionContext)channelContext.getAttribute();
		if(ImPacketType.TCP == sessionContext.getPacketType())
			return true;
		if(buffer != null){
			//获取第一个字节协议版本号;
			byte version = buffer.get();
			if(version == Protocol.VERSION ){//TCP协议;
				return true;
			}
		}else if(packet instanceof TcpPacket){
			return true;
		}
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext,ChannelContext channelContext) {
		TcpPacket tcpPacket = (TcpPacket)packet;
		if (tcpPacket.getCommand() == Command.COMMAND_HANDSHAKE_RESP) {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put(Protocol.HANDSHAKE_BYTE);
			return buffer;
		}else if(tcpPacket.getCommand() == Command.COMMAND_CHAT_RESP){
			
			return TcpResponseEncoder.encode(tcpPacket, groupContext, channelContext);
		}
		return null;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		TcpPacket tcpPacket = (TcpPacket)packet;
		String message = new String(tcpPacket.getBody(),Const.CHARSET);
		String onText = new String("服务器收到来自->"+channelContext.getId()+"的消息:"+message);
		System.out.println(onText);
		Map<String,Object> resultMap = Resps.convertResPacket(tcpPacket, channelContext);
		if(resultMap != null){
			ChannelContext toChnnelContext = (ChannelContext)resultMap.get(Const.CHANNEL);
			ImPacket imPacket = (ImPacket)resultMap.get(Const.PACKET);
			Aio.send(toChnnelContext, imPacket);
		}
	}

	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		imSessionContext.setPacketType(ImPacketType.TCP);
		return TcpRequestDecoder.decode(buffer, channelContext);
	}

	@Override
	public AbServerHandler build() {
		
		return new TcpServerHandler();
	}

	@Override
	public String name() {
		
		return Protocol.TCP;
	}
}
