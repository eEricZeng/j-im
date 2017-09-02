/**
 * 
 */
package org.tio.im.server.http;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpRequestDecoder;
import org.tio.http.common.session.HttpSession;
import org.tio.http.server.HttpServerAioHandler;
import org.tio.im.common.Const;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.ChatReqBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.im.server.init.HttpServerInit;
import org.tio.server.ServerGroupContext;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午3:07:54
 */
public class HttpServerHandler extends AbServerHandler{

	//private static Logger log = LoggerFactory.getLogger(HttpServerHandler.class);
	
	private HttpServerAioHandler httpServerAioHandler;
	
	private CommandManager commandManager = CommandManager.getInstance();
	public HttpServerHandler() {}
	
	public HttpServerHandler(HttpServerAioHandler httpServerAioHandler){
		this.httpServerAioHandler = httpServerAioHandler;
	}
	@Override
	public void init(ServerGroupContext serverGroupContext)throws Exception{
		HttpServerInit.init(serverGroupContext);
		this.httpServerAioHandler = HttpServerInit.httpServerAioHandler;
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
		return this.httpServerAioHandler.encode(packet, groupContext, channelContext);
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		this.httpServerAioHandler.handler(packet, channelContext);
	}

	@Override
	public HttpRequest decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		HttpRequest request = this.httpServerAioHandler.decode(buffer, channelContext);
		ChatReqBody chatBody = ImUtils.parseChatBody(request.getBodyString());
		if(chatBody != null){
			Integer cmd = chatBody.getCmd();
			if(cmd == null)
				cmd = Command.COMMAND_CHAT_REQ_VALUE;
			channelContext.setAttribute(Protocol.COMMAND,commandManager.getCommand(cmd));
		}else{
			channelContext.setAttribute(Protocol.COMMAND,null);
		}
		channelContext.setAttribute(Const.HTTP_REQUEST,request);
		return request;
	}
	
	@Override
	public AbServerHandler build() {
		return new HttpServerHandler(this.getHttpServerAioHandler());
	}
	
	public HttpServerAioHandler getHttpServerAioHandler() {
		return httpServerAioHandler;
	}

	public void setHttpServerAioHandler(HttpServerAioHandler httpServerAioHandler) {
		this.httpServerAioHandler = httpServerAioHandler;
	}

	@Override
	public String name() {
		
		return Protocol.HTTP;
	}
	
}
