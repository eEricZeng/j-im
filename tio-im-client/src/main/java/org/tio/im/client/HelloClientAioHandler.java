package org.tio.im.client;

import java.nio.ByteBuffer;

import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;
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
		HelloPacket helloPacket = (HelloPacket)packet;
		byte[] body = helloPacket.getBody();
		if (body != null)
		{
			String str = new String(body, HelloPacket.CHARSET);
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
		boolean isCompress = true;
		boolean is4ByteLength = true;
		boolean isEncrypt = true;
		byte firstbyte = HelloPacket.encodeEncrypt(HelloPacket.VERSION, isEncrypt);
		firstbyte = HelloPacket.encodeCompress(firstbyte, isCompress);
		firstbyte = HelloPacket.encodeHasSynSeq(firstbyte, packet.getSynSeq() > 0);
		firstbyte = HelloPacket.encode4ByteLength(firstbyte, is4ByteLength);
		firstbyte = (byte) (firstbyte| 11);//消息类型;
		HelloPacket helloPacket = (HelloPacket)packet;
		byte[] body = helloPacket.getBody();
		int bodyLen = 0;
		if (body != null)
		{
			bodyLen = body.length;
		}

		//bytebuffer的总长度是 = 1byte协议版本号+1byte消息标志位+4byte消息的长度+消息体的长度
		int allLen = 1+1+4+bodyLen;
		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		//设置字节序
		buffer.order(groupContext.getByteOrder());
		buffer.put(HelloPacket.VERSION);
		buffer.put(firstbyte);
		buffer.putInt(bodyLen);
		buffer.put(body);
		return buffer;
	}
	
	@Override
	public HelloPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		//获取第一个字节协议版本号;
		byte version = buffer.get();
		if(version != 0x01){
			throw new AioDecodeException("协议版本号不匹配");
		}
		byte maskByte = buffer.get();
		int command = maskByte & 0x0F;
		int bodyLen = buffer.getInt();
		int readableLength = buffer.limit() - buffer.position();
		//数据不正确，则抛出AioDecodeException异常
		if (readableLength < bodyLen)
		{
			throw new AioDecodeException("bodyLength [" + bodyLen + "] is not right, remote:" + channelContext.getClientNode());
		}
		byte[] body = new byte[bodyLen];
		buffer.get(body,0,bodyLen);
		System.out.println("TCP解码成功..."+command);
		//bytebuffer的总长度是 = 1byte协议版本号+1byte消息标志位+4byte消息的长度+消息体的长度
		HelloPacket tcpPacket = new HelloPacket();
		tcpPacket.setBody(body);
		return tcpPacket;
	}
	
	private static HelloPacket heartbeatPacket = new HelloPacket();

	/** 
	 * 此方法如果返回null，框架层面则不会发心跳；如果返回非null，框架层面会定时发本方法返回的消息包
	 */
	@Override
	public HelloPacket heartbeatPacket()
	{
		return heartbeatPacket;
	}
}
