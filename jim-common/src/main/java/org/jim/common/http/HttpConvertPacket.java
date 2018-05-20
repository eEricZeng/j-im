/**
 * 
 */
package org.jim.common.http;

import org.jim.common.Const;
import org.jim.common.ImPacket;
import org.jim.common.http.session.HttpSession;
import org.jim.common.packets.Command;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.tio.core.ChannelContext;

/**
 * HTTP协议消息转化包
 * @author WChao
 *
 */
public class HttpConvertPacket implements IConvertProtocolPacket {

	/**
	 * 转HTTP协议响应包;
	 */
	@Override
	public ImPacket RespPacket(byte[] body, Command command,ChannelContext channelContext) {
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext instanceof HttpSession){//
			HttpRequest request = (HttpRequest)channelContext.getAttribute(Const.HTTP_REQUEST);
			HttpResponse response = new HttpResponse(request,request.getHttpConfig());
			response.setBody(body, request);
			response.setCommand(command);
			return response;
		}
		return null;
	}

	@Override
	public ImPacket ReqPacket(byte[] body, Command command,ChannelContext channelContext) {
		
		return null;
	}

}
