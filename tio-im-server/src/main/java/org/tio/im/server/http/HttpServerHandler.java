/**
 * 
 */
package org.tio.im.server.http;

import java.nio.ByteBuffer;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.im.common.Const;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpRequestDecoder;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.http.HttpResponseEncoder;
import org.tio.im.common.http.handler.IHttpRequestHandler;
import org.tio.im.common.http.session.HttpSession;
import org.tio.im.common.packets.ChatReqBody;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.command.handler.ChatReqHandler;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.im.server.init.HttpServerInit;
import org.tio.server.ServerGroupContext;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午3:07:54
 */
public class HttpServerHandler extends AbServerHandler{

	private HttpConfig httpConfig;
	
	private IHttpRequestHandler httpRequestHandler;
	
	public HttpServerHandler() {}
	
	public HttpServerHandler(IHttpRequestHandler httpRequestHandler , HttpConfig httpServerConfig){
		this.httpRequestHandler = httpRequestHandler;
		this.httpConfig = httpServerConfig;
	}
	@Override
	public void init(ServerGroupContext serverGroupContext)throws Exception{
		HttpServerInit.init(serverGroupContext);
		this.httpConfig = HttpServerInit.httpConfig;
		this.httpRequestHandler = HttpServerInit.requestHandler;
	}
	
	@Override
	public boolean isProtocol(ByteBuffer buffer,ChannelContext channelContext)throws Throwable{
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){
			if(buffer != null){
				try{
					HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
					if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) == null)
					{
						channelContext.setAttribute(new HttpSession());
						return true;
					}
				}catch(Throwable e){
					e.printStackTrace();
				}
			}
		}else if(sessionContext instanceof HttpSession){
			return true;
		}
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext,ChannelContext channelContext) {
		HttpResponse httpResponsePacket = (HttpResponse) packet;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponsePacket, groupContext, channelContext,false);
		return byteBuffer;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		HttpRequest httpRequestPacket = (HttpRequest) packet;
		HttpResponse httpResponsePacket = httpRequestHandler.handler(httpRequestPacket, httpRequestPacket.getRequestLine());
		Aio.send(channelContext, httpResponsePacket);
	}

	@Override
	public Packet decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
		ChatReqBody chatBody = ChatReqHandler.parseChatBody(request.getBodyString());
		if(chatBody != null){
			Integer cmd = chatBody.getCmd();
			if(cmd == null)
				cmd = Command.COMMAND_CHAT_REQ_VALUE;
			channelContext.setAttribute(Protocol.COMMAND,CommandManager.getInstance().getCommand(cmd));
		}else{
			channelContext.setAttribute(Protocol.COMMAND,null);
		}
		channelContext.setAttribute(Const.HTTP_REQUEST,request);
		return request;
	}
	
	@Override
	public AbServerHandler build() {
		return new HttpServerHandler(this.getHttpRequestHandler(),this.getHttpConfig());
	}

	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
	
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	@Override
	public String name() {
		
		return Protocol.HTTP;
	}
	
}
