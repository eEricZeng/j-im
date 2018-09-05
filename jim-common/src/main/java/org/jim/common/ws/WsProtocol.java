/**
 * 
 */
package org.jim.common.ws;

import java.nio.ByteBuffer;

import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.Protocol;
import org.jim.common.http.HttpConst;
import org.jim.common.http.HttpRequest;
import org.jim.common.http.HttpRequestDecoder;
import org.jim.common.protocol.AbProtocol;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.jim.common.utils.ImUtils;
import org.tio.core.ChannelContext;

/**
 * WebSocket协议判断器
 * @author WChao
 *
 */
public class WsProtocol extends AbProtocol {

	@Override
	public String name() {
		return Protocol.WEBSOCKET;
	}
	
	@Override
	public boolean isProtocolByBuffer(ByteBuffer buffer,ChannelContext channelContext) throws Throwable {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		if(imSessionContext != null && imSessionContext instanceof WsSessionContext) {
			return true;
		}
		//第一次连接;
		if(buffer != null){
			HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext,false);
			if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null)
			{
				channelContext.setAttribute(new WsSessionContext());
				ImUtils.setClient(channelContext);
				return true;
			}
		}
		return false;
	}

	@Override
	public IConvertProtocolPacket converter() {
		return new WsConvertPacket();
	}
	
	@Override
	public boolean isProtocol(ImPacket imPacket,ChannelContext channelContext) throws Throwable {
		if(imPacket == null) {
			return false;
		}
		if(imPacket instanceof WsPacket){
			Object sessionContext = channelContext.getAttribute();
			if(sessionContext == null){
				channelContext.setAttribute(new WsSessionContext());
			}
			return true;
		}
		return false;
	}
}
