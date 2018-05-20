/**
 * 
 */
package org.jim.common.protocol;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;

/**
 * @author WChao
 *
 */
public abstract class AbProtocol implements IProtocol {
	//协议包转化器;
	private IConvertProtocolPacket convertor;
	
	public AbProtocol(){
		this.convertor = convertor();
	}
	public abstract boolean isProtoc(ByteBuffer buffer,ChannelContext channelContext) throws Throwable;
	@Override
	public boolean isProtocol(ByteBuffer buffer,ChannelContext channelContext) throws Throwable {
		ByteBuffer copyByteBuffer = null;
		if(buffer != null && channelContext.getAttribute() == null){
			copyByteBuffer = ByteBuffer.wrap(buffer.array());
		}
		return isProtoc(copyByteBuffer, channelContext);
	}
	public IConvertProtocolPacket getConvertor() {
		return convertor;
	}
}
