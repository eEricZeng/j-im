/**
 * 
 */
package org.tio.im.server.handler;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月4日 下午4:15:27
 */
public class DetaultServerHandler extends AbServerHandler{

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isProtocol(ByteBuffer buffer,Packet packet,ChannelContext channelContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext,ChannelContext channelContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public Packet decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbServerHandler build() {
		
		return new DetaultServerHandler();
	}

	@Override
	public String name() {
		
		return "default";
	}

}
