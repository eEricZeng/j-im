/**
 * 
 */
package org.tio.im.server.websocket;

import java.nio.ByteBuffer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImPacketType;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpPacket;
import org.tio.im.common.http.HttpRequestDecoder;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponseEncoder;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.websocket.WebSocketPacket.Opcode;
import org.tio.im.common.http.websocket.WebSocketRequestDecoder;
import org.tio.im.common.http.websocket.WebSocketRequestPacket;
import org.tio.im.common.http.websocket.WebSocketResponseEncoder;
import org.tio.im.common.http.websocket.WebSocketResponsePacket;
import org.tio.im.common.http.websocket.WebSocketSessionContext;
import org.tio.im.common.packets.Command;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.im.server.util.Resps;

import com.jfinal.kit.PropKit;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午6:38:36
 */
public class WebSocketServerHandler extends AbServerHandler{
	
	private Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);
	
	private Packet packet = null;
	
	private WsServerConfig wsServerConfig;

	private IWsMsgHandler wsMsgHandler;
	
	public WebSocketServerHandler() {}
	
	public WebSocketServerHandler(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) {
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsMsgHandler;
	}
	@Override
	public void init() {
		PropKit.use("app.properties");
		
		int port = PropKit.getInt("port");//启动端口
		this.wsServerConfig = new WsServerConfig(port);
		this.wsMsgHandler = new WsMsgHandler();
		log.info("WebSocketServerHandler初始化完毕...");
	}

	@Override
	public boolean isProtocol(ByteBuffer buffer,Packet packet,ChannelContext channelContext){
		ImSessionContext sessionContext = (ImSessionContext)channelContext.getAttribute();
		if(ImPacketType.WS == sessionContext.getPacketType())
			return true;
		if(buffer != null){
			byte first = buffer.get();
			byte opCodeByte = (byte) (first & 0x0F);//后四位为opCode 00001111
			Opcode opcode = Opcode.valueOf(opCodeByte);
			if(opcode != null)
				return true;
			try{
				HttpRequestPacket httpRequestPacket = HttpRequestDecoder.decode(buffer,false);
				if(httpRequestPacket.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null)
				{
					return true;
				}
			}catch(Throwable e){
				e.printStackTrace();
			}
		}else if(packet != null){
			if(packet instanceof WebSocketRequestPacket || packet instanceof WebSocketResponsePacket){
				return true;
			}else if(packet instanceof HttpPacket){
				HttpPacket httpPacket = (HttpPacket)packet;
				if(httpPacket.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		ImPacket imPacket = (ImPacket)packet;
		boolean isWebsocket = imSessionContext.isWebsocket();
		if (imPacket.getCommand() == Command.COMMAND_HANDSHAKE_RESP) {
			if (isWebsocket) {
				//握手包
				HttpResponsePacket handshakeResponsePacket = imSessionContext.getHandshakeResponsePacket();
				return HttpResponseEncoder.encode(handshakeResponsePacket, groupContext, channelContext);
			} else {
				ByteBuffer buffer = ByteBuffer.allocate(1);
				buffer.put(Protocol.HANDSHAKE_BYTE);
				return buffer;
			}
		}else if (isWebsocket) {
			return WebSocketResponseEncoder.encode(imPacket , groupContext, channelContext);
		}
		return null;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		
		WebSocketRequestPacket WebSocketRequestPacket = (WebSocketRequestPacket) packet;

		if (WebSocketRequestPacket.isHandShake()) {
			WebSocketSessionContext wsSessionContext = (WebSocketSessionContext)channelContext.getAttribute();;
			HttpRequestPacket httpRequestPacket = wsSessionContext.getHandshakeRequestPacket();
			HttpResponsePacket httpResponsePacket = wsSessionContext.getHandshakeResponsePacket();
			WebSocketResponsePacket wsResponsePacket = wsMsgHandler.handshake(httpRequestPacket, httpResponsePacket, channelContext);
			if (wsResponsePacket == null) {
				Aio.remove(channelContext, "业务层不同意握手");
			}
			Aio.send(channelContext, wsResponsePacket);
		}else{
			if(WebSocketRequestPacket.getWsOpcode() == Opcode.CLOSE){
				WebSocketResponsePacket WebSocketResponsePacket = h(WebSocketRequestPacket, WebSocketRequestPacket.getBody(), WebSocketRequestPacket.getWsOpcode(), channelContext);
				if (WebSocketResponsePacket != null) {
					Aio.send(channelContext, WebSocketResponsePacket);
				}
			}else{
				Map<String,Object> resultMap = Resps.convertResPacket(WebSocketRequestPacket, channelContext);
				if(resultMap != null){
					ChannelContext toChnnelContext = (ChannelContext)resultMap.get(Const.CHANNEL);
					ImPacket imPacket = (ImPacket)resultMap.get(Const.PACKET);
					Aio.send(toChnnelContext, imPacket);
				}
			}
		}
	}

	public WebSocketResponsePacket h(WebSocketRequestPacket websocketPacket, byte[] bytes, Opcode opcode, ChannelContext channelContext) throws Exception {
		WebSocketResponsePacket WebSocketResponsePacket = null;
		if (opcode == Opcode.TEXT) {
			if (bytes == null || bytes.length == 0) {
				Aio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			String text = new String(bytes, wsServerConfig.getCharset());
			Object retObj = wsMsgHandler.onText(websocketPacket, text, channelContext);
			String methodName = "onText";
			WebSocketResponsePacket = processRetObj(retObj, methodName, channelContext);
			return WebSocketResponsePacket;
		} else if (opcode == Opcode.BINARY) {
			if (bytes == null || bytes.length == 0) {
				Aio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			Object retObj = wsMsgHandler.onBytes(websocketPacket, bytes, channelContext);
			String methodName = "onBytes";
			WebSocketResponsePacket = processRetObj(retObj, methodName, channelContext);
			return WebSocketResponsePacket;
		} else if (opcode == Opcode.PING || opcode == Opcode.PONG) {
			log.error("收到" + opcode);
			return null;
		} else if (opcode == Opcode.CLOSE) {
			Object retObj = wsMsgHandler.onClose(websocketPacket, bytes, channelContext);
			String methodName = "onClose";
			WebSocketResponsePacket = processRetObj(retObj, methodName, channelContext);
			return WebSocketResponsePacket;
		} else {
			Aio.remove(channelContext, "错误的websocket包，错误的Opcode");
			return null;
		}
	}

	private WebSocketResponsePacket processRetObj(Object obj, String methodName, ChannelContext channelContext) throws Exception {
		WebSocketResponsePacket WebSocketResponsePacket = null;
		if (obj == null) {
			return null;
		} else {
			if (obj instanceof String) {
				String str = (String) obj;
				WebSocketResponsePacket = new WebSocketResponsePacket();
				WebSocketResponsePacket.setBody(str.getBytes(wsServerConfig.getCharset()));
				WebSocketResponsePacket.setWsOpcode(Opcode.TEXT);
				return WebSocketResponsePacket;
			} else if (obj instanceof byte[]) {
				WebSocketResponsePacket = new WebSocketResponsePacket();
				WebSocketResponsePacket.setBody((byte[]) obj);
				WebSocketResponsePacket.setWsOpcode(Opcode.BINARY);
				return WebSocketResponsePacket;
			} else if (obj instanceof WebSocketResponsePacket) {
				return (WebSocketResponsePacket) obj;
			} else if (obj instanceof ByteBuffer) {
				WebSocketResponsePacket = new WebSocketResponsePacket();
				byte[] bs = ((ByteBuffer) obj).array();
				WebSocketResponsePacket.setBody(bs);
				WebSocketResponsePacket.setWsOpcode(Opcode.BINARY);
				return WebSocketResponsePacket;
			} else {
				log.error("{} {}.{}()方法，只允许返回byte[]、ByteBuffer、WebSocketResponsePacket或null，但是程序返回了{}", channelContext, this.getClass().getName(), methodName, obj.getClass().getName());
				return null;
			}
		}
		
	}

	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		imSessionContext.setPacketType(ImPacketType.WS);
		boolean isWebSocket = imSessionContext.isWebsocket();//是否WebSocket协议;
		boolean isHandshaked = imSessionContext.isHandshaked();//是否握手完毕;
		if(!(isWebSocket && isHandshaked)){//WebSocket协议;
			HttpRequestPacket  httpRequestPacket = HttpRequestDecoder.decode(buffer,true);
			if(httpRequestPacket == null)
				return null;
			//升级到WebSocket协议处理
			WebSocketRequestPacket WebSocketRequestPacket = WebSocketRequestDecoder.updateRequestPacketProtocol(httpRequestPacket,channelContext);
			if (WebSocketRequestPacket == null) {
				throw new AioDecodeException("http协议升级到websocket协议失败");
			}
			return WebSocketRequestPacket;
		}else{
			WebSocketRequestPacket websocketPacket = WebSocketRequestDecoder.decode(buffer, channelContext);
			websocketPacket.setCommand(Command.COMMAND_WEBSOCKET_REQ);
			return websocketPacket;
		}
	}

	@Override
	public AbServerHandler build() {
		
		return new WebSocketServerHandler(this.wsServerConfig,this.wsMsgHandler);
	}

	public Packet getPacket() {
		return packet;
	}

	public WebSocketServerHandler setPacket(Packet packet) {
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
