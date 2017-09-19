/**
 * 
 */
package org.tio.im.server.http.api;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.server.command.handler.ChatReqHandler;
import org.tio.im.server.http.annotation.RequestPath;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月8日 上午9:08:48
 */
@RequestPath(value = "/api")
public class HttpApiController {
	
	@RequestPath(value = "/message/send")
	public HttpResponse json(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		HttpResponse response = new HttpResponse(request,httpConfig);
		ChatBody chatBody = ChatReqHandler.parseChatBody(request.getBody(), channelContext);//转化成消息结构体;
		ImPacket chatRespPacket = ChatReqHandler.convertChatResPacket(chatBody, channelContext);//转换成不同的协议响应包;
		ChannelContext toChannleContext = ChatReqHandler.getToChannel(chatBody, channelContext.getGroupContext());//获取目标channel;
		if(chatRespPacket.getStatus() == ImStatus.C1){
			Aio.send(toChannleContext, chatRespPacket);
			response.setBody(ChatReqHandler.toImStatusBody(ImStatus.C1),request);
		}else{
			response = (HttpResponse)chatRespPacket;
		}
		return response;
	}
}
