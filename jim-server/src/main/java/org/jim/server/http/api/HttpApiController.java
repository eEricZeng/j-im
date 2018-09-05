/**
 * 
 */
package org.jim.server.http.api;

import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.http.HttpConfig;
import org.jim.common.http.HttpRequest;
import org.jim.common.http.HttpResponse;
import org.jim.common.packets.CloseReqBody;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.ChatReqHandler;
import org.jim.server.command.handler.CloseReqHandler;
import org.jim.server.http.annotation.RequestPath;
import org.jim.server.util.HttpResps;
import org.tio.core.ChannelContext;
/**
 * 版本: [1.0]
 * 功能说明: Http协议消息发送控制器类
 * @author : WChao 创建时间: 2017年8月8日 上午9:08:48
 */
@RequestPath(value = "/api")
public class HttpApiController {
	
	@RequestPath(value = "/message/send")
	public HttpResponse chat(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		HttpResponse response = new HttpResponse(request,httpConfig);
		ChatReqHandler chatReqHandler = CommandManager.getCommand(Command.COMMAND_CHAT_REQ,ChatReqHandler.class);
		ImPacket chatPacket = chatReqHandler.handler(request, channelContext);
		if(chatPacket != null){
			response = (HttpResponse)chatPacket;
		}
		return response;
	}
	/**
	 * 判断用户是否在线接口;
	 * @param request
	 * @param httpConfig
	 * @param channelContext
	 * @return
	 * @throws Exception
	 */
	@RequestPath(value = "/user/online")
	public HttpResponse online(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		Object[] params = request.getParams().get("userid");
		if(params == null || params.length == 0){
			return HttpResps.json(request, new RespBody(ImStatus.C10020));
		}
		String userId = params[0].toString();
		User user = ImAio.getUser(userId);
		if(user != null){
			return HttpResps.json(request, new RespBody(ImStatus.C10019));
		}else{
			return HttpResps.json(request, new RespBody(ImStatus.C10001));
		}
	}
	/**
	 * 关闭指定用户;
	 * @param request
	 * @param httpConfig
	 * @param channelContext
	 * @return
	 * @throws Exception
	 */
	@RequestPath(value = "user/close")
	public HttpResponse close(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		Object[] params = request.getParams().get("userid");
		if(params == null || params.length == 0){
			return HttpResps.json(request, new RespBody(ImStatus.C10020));
		}
		String userId = params[0].toString();
		ImPacket closePacket = new ImPacket(Command.COMMAND_CLOSE_REQ,new CloseReqBody(userId).toByte());
		CloseReqHandler closeReqHandler = CommandManager.getCommand(Command.COMMAND_CLOSE_REQ,CloseReqHandler.class);
		closeReqHandler.handler(closePacket, channelContext);
		return HttpResps.json(request, new RespBody(ImStatus.C10021));
	}
}
