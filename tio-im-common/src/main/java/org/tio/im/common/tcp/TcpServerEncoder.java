/**
 * 
 */
package org.tio.im.common.tcp;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.Protocol;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月21日 下午4:00:31
 */
public class TcpServerEncoder {

	public static ByteBuffer encode(TcpPacket tcpPacket, GroupContext groupContext, ChannelContext channelContext){
		int bodyLen = 0;
		byte[] body = tcpPacket.getBody();
		if (body != null)
		{
			bodyLen = body.length;
		}
		boolean isCompress = true;
		boolean is4ByteLength = true;
		boolean isEncrypt = true;
		//协议版本号
		byte version = Protocol.VERSION;
		
		//协议标志位mask
		byte maskByte = ImPacket.encodeEncrypt(version, isEncrypt);
		maskByte = ImPacket.encodeCompress(maskByte, isCompress);
		maskByte = ImPacket.encodeHasSynSeq(maskByte, tcpPacket.getSynSeq() > 0);
		maskByte = ImPacket.encode4ByteLength(maskByte, is4ByteLength);
		byte cmdByte = 0x00;
		if(tcpPacket.getCommand() != null)
		cmdByte = (byte) (cmdByte|tcpPacket.getCommand().getNumber());//消息类型;
		
		tcpPacket.setVersion(version);
		tcpPacket.setMask(maskByte);
		
		//bytebuffer的总长度是 = 1byte协议版本号+1byte消息标志位+1byte命令码+4byte消息的长度+消息体
		int allLen = 1+1+1+4+bodyLen;
		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		//设置字节序
		buffer.order(groupContext.getByteOrder());
		buffer.put(tcpPacket.getVersion());
		buffer.put(tcpPacket.getMask());
		buffer.put(cmdByte);
		buffer.putInt(bodyLen);
		buffer.put(body);
		return buffer;
	}
}
