/**
 * 
 */
package org.jim.server.ws;

import java.nio.ByteBuffer;

import org.jim.common.ImAio;
import org.jim.common.ImConfig;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.jim.common.http.HttpConst;
import org.jim.common.http.HttpRequest;
import org.jim.common.http.HttpRequestDecoder;
import org.jim.common.http.HttpResponse;
import org.jim.common.http.HttpResponseEncoder;
import org.jim.common.packets.Command;
import org.jim.common.packets.Message;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ImUtils;
import org.jim.common.utils.JsonKit;
import org.jim.common.ws.IWsMsgHandler;
import org.jim.common.ws.Opcode;
import org.jim.common.ws.WsRequestPacket;
import org.jim.common.ws.WsResponsePacket;
import org.jim.common.ws.WsServerConfig;
import org.jim.common.ws.WsServerDecoder;
import org.jim.common.ws.WsServerEncoder;
import org.jim.common.ws.WsSessionContext;
import org.jim.server.command.AbCmdHandler;
import org.jim.server.command.CommandManager;
import org.jim.server.handler.AbServerHandler;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午6:38:36
 */
public class WsServerHandler extends AbServerHandler{
	
	private Logger logger = LoggerFactory.getLogger(WsServerHandler.class);
	
	private WsServerConfig wsServerConfig;

	private IWsMsgHandler wsMsgHandler;
	
	public WsServerHandler() {}
	
	public WsServerHandler(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) {
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsMsgHandler;
	}
	@Override
	public void init(ImConfig imConfig) {
		this.wsServerConfig = new WsServerConfig();
		imConfig.setWsServerConfig(wsServerConfig);
		this.wsMsgHandler = new WsMsgHandler();
		this.wsServerConfig.setWsMsgHandler(wsMsgHandler);
		logger.info("wsServerHandler 初始化完毕...");
	}

	@Override
	public boolean isProtocol(ByteBuffer buffer,ChannelContext channelContext)throws Throwable{
		if(buffer != null){//第一次连接;
			HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext,false);
			if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null)
			{
				channelContext.setAttribute(new WsSessionContext().setServerHandler(this));
				ImUtils.setClient(channelContext);
				return true;
			}
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
		AbCmdHandler cmdHandler = CommandManager.getCommand(wsRequestPacket.getCommand());
		if(cmdHandler == null){
			if(!wsRequestPacket.isWsEof())//是否ws分片发包尾帧包
				return;
			ImPacket imPacket = new ImPacket(Command.COMMAND_UNKNOW, new RespBody(Command.COMMAND_UNKNOW,ImStatus.C10017).toByte());
			ImAio.send(channelContext, imPacket);
			return;
		}
		Object response = cmdHandler.handler(wsRequestPacket, channelContext);
		if(response != null){
			ImAio.send(channelContext, (ImPacket)response);
		}
	}

	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		WsSessionContext wsSessionContext = (WsSessionContext)channelContext.getAttribute();
		if(!wsSessionContext.isHandshaked()){//握手
			HttpRequest  httpRequest = HttpRequestDecoder.decode(buffer,channelContext,true);
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
			wsRequestPacket.setCommand(Command.COMMAND_HANDSHAKE_REQ);
			return wsRequestPacket;
		}else{
			WsRequestPacket wsRequestPacket = WsServerDecoder.decode(buffer, channelContext);
			if(wsRequestPacket == null)
				return null;
			Command command = null;
			if(wsRequestPacket.getWsOpcode() == Opcode.CLOSE){
				command = Command.COMMAND_CLOSE_REQ;
			}else{
				try{
					Message message = JsonKit.toBean(wsRequestPacket.getBody(),Message.class);
					command = Command.forNumber(message.getCmd());
				}catch(Exception e){
					return wsRequestPacket;
				}
			}
			wsRequestPacket.setCommand(command);
			return wsRequestPacket;
		}
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
