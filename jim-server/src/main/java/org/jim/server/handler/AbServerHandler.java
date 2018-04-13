/**
 * 
 */
package org.jim.server.handler;

import java.nio.ByteBuffer;

import org.jim.common.ImConfig;
import org.tio.core.ChannelContext;
import org.tio.server.intf.ServerAioHandler;
/**
 * 版本: [1.0] 功能说明: 封装tioServerAioHandler，提供更丰富的方法供客户端定制化;
 * 作者: WChao 创建时间: 2017年8月3日 上午9:47:44
 */
public abstract class AbServerHandler implements ServerAioHandler{
	public abstract String name();
	public abstract void init(ImConfig imConfig)throws Exception;
	public abstract boolean isProtocol(ByteBuffer byteBuffer,ChannelContext channelContext)throws Throwable;
}
