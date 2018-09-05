/**
 * 
 */
package org.jim.common.tcp;

import java.nio.ByteBuffer;

import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.Protocol;
import org.jim.common.protocol.AbProtocol;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.jim.common.utils.ImUtils;
import org.tio.core.ChannelContext;

/**
 * Tcp协议判断器
 * @author WChao
 *
 */
public class TcpProtocol extends AbProtocol {

	@Override
	public String name() {
		return Protocol.TCP;
	}

	@Override
	public boolean isProtocolByBuffer(ByteBuffer buffer,ChannelContext channelContext) throws Throwable {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		if(imSessionContext != null && imSessionContext instanceof TcpSessionContext) {
			return true;
		}
		if(buffer != null){
			//获取第一个字节协议版本号;
			byte version = buffer.get();
			//TCP协议;
			if(version == Protocol.VERSION){
				channelContext.setAttribute(new TcpSessionContext());
				ImUtils.setClient(channelContext);
				return true;
			}
		}
		return false;
	}

	@Override
	public IConvertProtocolPacket converter() {
		return new TcpConvertPacket();
	}
	
	@Override
	public boolean isProtocol(ImPacket imPacket,ChannelContext channelContext) throws Throwable {
		if(imPacket == null) {
			return false;
		}
		if(imPacket instanceof TcpPacket){
			Object sessionContext = channelContext.getAttribute();
			if(sessionContext == null){
				channelContext.setAttribute(new TcpSessionContext());
			}
			return true;
		}
		return false;
	}
}
