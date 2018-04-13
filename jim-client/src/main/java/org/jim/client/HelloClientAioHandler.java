package org.jim.client;

import java.nio.ByteBuffer;

import org.jim.common.Const;
import org.jim.common.Protocol;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;
import org.jim.common.packets.Command;
import org.jim.common.tcp.TcpPacket;
import org.jim.common.tcp.TcpServerDecoder;
import org.jim.common.tcp.TcpServerEncoder;
/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月30日 下午1:10:28
 */
public class HelloClientAioHandler  implements AioHandler,ClientAioHandler
{
	/** 
	 * 处理消息
	 */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception
	{
		TcpPacket helloPacket = (TcpPacket)packet;
		byte[] body = helloPacket.getBody();
		if (body != null)
		{
			String str = new String(body, Const.CHARSET);
			System.out.println("收到消息：" + str);
		}

		return;
	}
	/**
	 * 编码：把业务消息包编码为可以发送的ByteBuffer
	 * 总的消息结构：消息头 + 消息体
	 * 消息头结构：    4个字节，存储消息体的长度
	 * 消息体结构：   对象的json串的byte[]
	 */
	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext)
	{
		TcpPacket tcpPacket = (TcpPacket)packet;
		return TcpServerEncoder.encode(tcpPacket, groupContext, channelContext);
	}
	
	@Override
	public TcpPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		TcpPacket tcpPacket = TcpServerDecoder.decode(buffer, channelContext);
		return tcpPacket;
	}
	
	private static TcpPacket heartbeatPacket = new TcpPacket(Command.COMMAND_HEARTBEAT_REQ,new byte[]{Protocol.HEARTBEAT_BYTE});

	/** 
	 * 此方法如果返回null，框架层面则不会发心跳；如果返回非null，框架层面会定时发本方法返回的消息包
	 */
	@Override
	public TcpPacket heartbeatPacket()
	{
		return heartbeatPacket;
	}
}
