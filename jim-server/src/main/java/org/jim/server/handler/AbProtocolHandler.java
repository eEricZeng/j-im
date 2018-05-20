/**
 * 
 */
package org.jim.server.handler;

import java.nio.ByteBuffer;

import org.jim.common.ImConfig;
import org.jim.common.protocol.IProtocol;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;
/**
 * 版本: [1.0] 功能说明: 封装tioServerAioHandler，提供更丰富的方法供客户端定制化;
 * 作者: WChao 创建时间: 2017年8月3日 上午9:47:44
 */
public abstract class AbProtocolHandler implements ServerAioHandler{
	public abstract IProtocol protocol();
	public abstract void init(ImConfig imConfig)throws Exception;
	public abstract Packet decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException;
	@Override
	public Packet decode(ByteBuffer buffer, int limit, int position,int readableLength, ChannelContext channelContext)throws AioDecodeException {
		return decode(buffer,channelContext);
	}
	
}
