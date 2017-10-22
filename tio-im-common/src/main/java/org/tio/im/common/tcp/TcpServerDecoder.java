/**
 * 
 */
package org.tio.im.common.tcp;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.Command;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月21日 下午3:08:04
 */
public class TcpServerDecoder {
	
	private static Logger logger = Logger.getLogger(TcpServerDecoder.class);
	
	public static TcpPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException{
		//获取第一个字节协议版本号;
		byte version = buffer.get();
		if(version != Protocol.VERSION){
			throw new AioDecodeException("协议版本号不匹配!");
		}
		byte maskByte = buffer.get();
		//cmd命令码
		byte cmdByte = buffer.get();
		if(Command.forNumber(cmdByte) == null){
			throw new AioDecodeException("不支持的cmd命令...");
		}
		int bodyLen = buffer.getInt();
		int readableLength = buffer.limit() - buffer.position();
		//数据不正确，则抛出AioDecodeException异常
		if (readableLength < bodyLen)
		{
			throw new AioDecodeException("bodyLength [" + bodyLen + "] is not right, remote:" + channelContext.getClientNode());
		}
		byte[] body = new byte[bodyLen];
		buffer.get(body,0,bodyLen);
		logger.info("TCP解码成功...");
		//bytebuffer的总长度是 = 1byte协议版本号+1byte消息标志位+1byte命令码+4byte消息的长度+消息体的长度
		TcpPacket tcpPacket = new TcpPacket(Command.forNumber(cmdByte), body);
		tcpPacket.setVersion(version);
		tcpPacket.setMask(maskByte);
		return tcpPacket;
	}
}
