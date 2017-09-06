/**
 * 
 */
package org.tio.im.server.ws;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.im.common.ImPacket;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpRequestDecoder;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.http.HttpResponseEncoder;
import org.tio.im.common.packets.Command;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.common.ws.WsRequestPacket;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.common.ws.WsServerDecoder;
import org.tio.im.common.ws.WsServerEncoder;
import org.tio.im.common.ws.WsSessionContext;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.server.ServerGroupContext;

import com.jfinal.kit.PropKit;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午6:38:36
 */
public class WsServerHandler extends AbServerHandler{
	
	private Logger log = LoggerFactory.getLogger(WsServerHandler.class);
	
	private Packet packet = null;
	
	private WsServerConfig wsServerConfig;

	private IWsMsgHandler wsMsgHandler;
	
	public WsServerHandler() {}
	
	public WsServerHandler(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) {
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsMsgHandler;
	}
	@Override
	public void init(ServerGroupContext serverGroupContext) {
		PropKit.use("app.properties");
		int port = PropKit.getInt("port");//启动端口
		this.wsServerConfig = new WsServerConfig(port);
		this.wsMsgHandler = new WsMsgHandler();
		log.info("WebSocketServerHandler初始化完毕...");
	}

	@Override
	public boolean isProtocol(ByteBuffer buffer,ChannelContext channelContext){
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){//第一次连接;
			if(buffer != null){
				try{
					HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
					if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null)
					{
						channelContext.setAttribute(new WsSessionContext());
						ImUtils.setClient(channelContext);
						return true;
					}
				}catch(Throwable e){
					e.printStackTrace();
				}
			}
		}else if(sessionContext instanceof WsSessionContext){
			return true;
		}
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		WsSessionContext wsSessionContext = (WsSessionContext)channelContext.getAttribute();
		WsResponsePacket wsResponsePacket = (WsResponsePacket)packet;
		if (wsResponsePacket.getCommand() == Command.COMMAND_HANDSHAKE_RESP) {
			//握手包
			HttpResponse handshakeResponsePacket = wsSessionContext.getHandshakeResponsePacket();
			return HttpResponseEncoder.encode(handshakeResponsePacket, groupContext, channelContext,true);
		}else{
			return WsServerEncoder.encode(wsResponsePacket , groupContext, channelContext);
		}
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		
		WsRequestPacket wsRequestPacket = (WsRequestPacket) packet;

		if (wsRequestPacket.isHandShake()) {
			WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
			HttpRequest request = wsSessionContext.getHandshakeRequestPacket();
			HttpResponse httpResponse = wsSessionContext.getHandshakeResponsePacket();
			WsResponsePacket wsResponse = wsMsgHandler.handshake(request, httpResponse, channelContext);
			if (wsResponse == null) {
				Aio.remove(channelContext, "业务层不同意握手");
				return;
			}
			Aio.send(channelContext, wsResponse);
			return;
		}

		Object wsResponsePacket = wsMsgHandler.handler(wsRequestPacket, channelContext);

		if (wsResponsePacket != null) {
			Aio.send(channelContext, (WsResponsePacket)wsResponsePacket);
		}

		return;
	}

	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		WsSessionContext wsSessionContext = (WsSessionContext)channelContext.getAttribute();
		if(!wsSessionContext.isHandshaked()){//握手
			HttpRequest  httpRequest = HttpRequestDecoder.decode(buffer,channelContext);
			if(httpRequest == null)
				return null;
			//升级到WebSocket协议处理
			HttpResponse httpResponse = WsServerDecoder.updateWebSocketProtocol(httpRequest,channelContext);
			if (httpResponse == null) {
				throw new AioDecodeException("http协议升级到websocket协议失败");
			}
			wsSessionContext.setHandshakeRequestPacket(httpRequest);
			wsSessionContext.setHandshakeResponsePacket(httpResponse);

			WsRequestPacket wsRequestPacket = new WsRequestPacket();
			wsRequestPacket.setHandShake(true);

			return wsRequestPacket;
		}else{
			WsRequestPacket wsRequestPacket = WsServerDecoder.decode(buffer, channelContext);
			wsRequestPacket.setCommand(Command.COMMAND_WEBSOCKET_REQ);
			return wsRequestPacket;
		}
	}

	@Override
	public AbServerHandler build() {
		
		return new WsServerHandler(this.wsServerConfig,this.wsMsgHandler);
	}

	public Packet getPacket() {
		return packet;
	}

	public WsServerHandler setPacket(Packet packet) {
		this.packet = packet;
		return this;
	}

	public WsServerConfig getWsServerConfig() {
		return wsServerConfig;
	}

	public void setWsServerConfig(WsServerConfig wsServerConfig) {
		this.wsServerConfig = wsServerConfig;
	}

	public IWsMsgHandler getWsMsgHandler() {
		return wsMsgHandler;
	}

	public void setWsMsgHandler(IWsMsgHandler wsMsgHandler) {
		this.wsMsgHandler = wsMsgHandler;
	}

	@Override
	public String name() {
		
		return Protocol.WEBSOCKET;
	}

}
