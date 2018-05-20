/**
 * 
 */
package org.jim.common.protocol;

import java.nio.ByteBuffer;

import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;

/**
 * 判断协议接口
 * @author WChao
 *
 */
public interface IProtocol {
	public abstract String name();
	public abstract boolean isProtocol(ByteBuffer byteBuffer,ChannelContext channelContext)throws Throwable;
	public abstract boolean isProtocol(ImPacket imPacket,ChannelContext channelContext)throws Throwable;
	public abstract IConvertProtocolPacket convertor();
}
