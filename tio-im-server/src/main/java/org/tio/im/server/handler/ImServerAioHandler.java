package org.tio.im.server.handler;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;
/**
 * 
 * @author WChao 
 *
 */
public class ImServerAioHandler implements ServerAioHandler {

	/** 
	 * @see org.tio.core.intf.AioHandler#handler(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @throws Exception 
	 * @author: Wchao
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		AbServerHandler handler = ServerHandlerManager.getServerHandler(null,channelContext);
		if(handler != null){
			handler.handler(packet, channelContext);
		}
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#encode(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @author: Wchao
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		AbServerHandler handler = ServerHandlerManager.getServerHandler(null,channelContext);
		if(handler != null){
			return handler.encode(packet, groupContext, channelContext);
		}
		return null;
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#decode(java.nio.ByteBuffer)
	 * 
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 * @author: Wchao
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public Packet decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		AbServerHandler handler = ServerHandlerManager.getServerHandler(buffer,channelContext);
		if(handler != null){
			return handler.decode(buffer, channelContext);
		}
		return null;
	}
}
