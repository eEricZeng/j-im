/**
 * 
 */
package org.jim.server.handler;

import org.jim.common.ImConfig;
import org.jim.common.protocol.IProtocol;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;
/**
 * 版本: [1.0] 功能说明: 封装tioServerAioHandler，提供更丰富的方法供客户端定制化;
 * @author : WChao 创建时间: 2017年8月3日 上午9:47:44
 */
public abstract class AbstractProtocolHandler implements ServerAioHandler{
	/**
	 * 获取不同协议管理器
	 * @return
	 */
	public abstract IProtocol protocol();

	/**
	 * 初始化不同协议
	 * @param imConfig
	 * @throws Exception
	 */
	public abstract void init(ImConfig imConfig)throws Exception;

	/**
	 * 将数据解码为消息Packet包
	 * @param buffer
	 * @param channelContext
	 * @return
	 * @throws AioDecodeException
	 */
	public abstract Packet decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException;

	/**
	 * t-io解码适配方法
	 * @param buffer
	 * @param limit
	 * @param position
	 * @param readableLength
	 * @param channelContext
	 * @return
	 * @throws AioDecodeException
	 */
	@Override
	public Packet decode(ByteBuffer buffer, int limit, int position,int readableLength, ChannelContext channelContext)throws AioDecodeException {
		return decode(buffer,channelContext);
	}
	
}
