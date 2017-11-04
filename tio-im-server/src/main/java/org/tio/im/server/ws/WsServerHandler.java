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
import org.tio.im.common.ImConfig;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpRequestDecoder;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.http.HttpResponseEncoder;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.Message;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.common.utils.Resps;
import org.tio.im.common.ws.Opcode;
import org.tio.im.common.ws.WsRequestPacket;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.common.ws.WsServerConfig;
import org.tio.im.common.ws.WsServerDecoder;
import org.tio.im.common.ws.WsServerEncoder;
import org.tio.im.common.ws.WsSessionContext;
import org.tio.im.server.command.CmdHandler;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.server.ServerGroupContext;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
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
	public void init(ServerGroupContext serverGroupContext,ImConfig imConfig) {
		PropKit.use("app.properties");
		int port = PropKit.getInt("port");//启动端口
		this.wsServerConfig = new WsServerConfig(port);
		this.wsMsgHandler = new WsMsgHandler();
		logger.info("wsServerHandler 初始化完毕...");
	}

	@Override
	public boolean isProtocol(ByteBuffer buffer,ChannelContext channelContext)throws Throwable{
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){//第一次连接;
			if(buffer != null){
				HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
				if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null)
				{
					channelContext.setAttribute(new WsSessionContext());
					ImUtils.setClient(channelContext);
					return true;
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
		CmdHandler cmdHandler = CommandManager.getInstance().getCommand(wsRequestPacket.getCommand());
		if(cmdHandler == null){
			RespBody respBody = new RespBody().setCode(ImStatus.C2.getCode()).setMsg(ImStatus.C2.getText()).setCommand(Command.COMMAND_UNKNOW);
			ImPacket responsePacket = Resps.convertRespPacket(respBody, channelContext);
			Aio.send(channelContext, responsePacket);
			return;
		}
		Object response = cmdHandler.handler(wsRequestPacket, channelContext);
		if(response != null){
			Aio.send(channelContext, (ImPacket)response);
		}
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
			wsRequestPacket.setCommand(Command.COMMAND_HANDSHAKE_REQ);
			return wsRequestPacket;
		}else{
			WsRequestPacket wsRequestPacket = WsServerDecoder.decode(buffer, channelContext);
			Command command = null;
			if(wsRequestPacket.getWsOpcode() == Opcode.CLOSE){
				command = Command.COMMAND_CLOSE_REQ;
			}else{
				try{
					Message message = JSONObject.parseObject(wsRequestPacket.getBody(),Message.class);
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
