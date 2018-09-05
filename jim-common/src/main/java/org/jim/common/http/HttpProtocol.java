/**
 * 
 */
package org.jim.common.http;

import java.nio.ByteBuffer;

import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.Protocol;
import org.jim.common.http.session.HttpSession;
import org.jim.common.protocol.AbProtocol;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.jim.common.utils.ImUtils;
import org.tio.core.ChannelContext;

/**
 *
 * Http协议校验器
 * @author WChao
 *
 */
public class HttpProtocol extends AbProtocol {

	@Override
	public String name() {
		return Protocol.HTTP;
	}

	@Override
	public boolean isProtocolByBuffer(ByteBuffer buffer,ChannelContext channelContext) throws Throwable {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		if(imSessionContext != null && imSessionContext instanceof HttpSession) {
			return true;
		}
		if(buffer != null){
			HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext,false);
			if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) == null)
			{
				channelContext.setAttribute(new HttpSession());
				ImUtils.setClient(channelContext);
				return true;
			}
		}
		return false;
	}

	@Override
	public IConvertProtocolPacket converter() {
		return new HttpConvertPacket();
	}

	@Override
	public boolean isProtocol(ImPacket imPacket,ChannelContext channelContext) throws Throwable {
		if(imPacket == null) {
			return false;
		}
		if(imPacket instanceof HttpPacket){
			Object sessionContext = channelContext.getAttribute();
			if(sessionContext == null){
				channelContext.setAttribute(new HttpSession());
			}
			return true;
		}
		return false;
	}

}
