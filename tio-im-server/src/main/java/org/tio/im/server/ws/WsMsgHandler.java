package org.tio.im.server.ws;

import java.nio.ByteBuffer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.packets.Command;
import org.tio.im.common.ws.Opcode;
import org.tio.im.common.ws.WsRequestPacket;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.common.ws.WsSessionContext;
import org.tio.im.server.command.handler.ChatReqHandler;
import org.tio.im.server.util.Resps;
/**
 * @author tanyaowu 
 * 2017年6月28日 下午5:32:38
 */
public class WsMsgHandler implements IWsMsgHandler{
	private static Logger log = LoggerFactory.getLogger(WsMsgHandler.class);

	private WsServerConfig wsServerConfig = null;

	/** 
	 * @param httpRequestPacket
	 * @param httpResponsePacket
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public WsResponsePacket handshake(HttpRequest request, HttpResponse response,ChannelContext channelContext) throws Exception {
		WsSessionContext wsSessionContext = (WsSessionContext)channelContext.getAttribute();
		WsResponsePacket wsResponsePacket = new WsResponsePacket();
		wsResponsePacket.setHandShake(true);
		wsResponsePacket.setCommand(Command.COMMAND_HANDSHAKE_RESP);
		wsSessionContext.setHandshaked(true);
		return wsResponsePacket;
	}

	/**
	 * 
	 * @param websocketPacket
	 * @param text
	 * @param channelContext
	 * @return 可以是WsResponsePacket、String、null
	 * @author: tanyaowu
	 */
	@Override
	public Object onText(WsRequestPacket wsRequestPacket, String text, ChannelContext channelContext) throws Exception {
		Map<String,Object> resultMap = Resps.convertResPacket(wsRequestPacket.getBody(), channelContext);
		ChannelContext toChannleContext = (ChannelContext)resultMap.get(Const.CHANNEL);
		Packet packet = (Packet)resultMap.get(Const.PACKET);
		ImStatus status = (ImStatus)resultMap.get(Const.STATUS);
		if(toChannleContext != channelContext){//不发送给自己;
			Aio.send(toChannleContext, packet);
		}
		text = new String(ChatReqHandler.toChatRespBody(status),HttpConst.CHARSET_NAME);
		return text;
	}

	/**
	 * 
	 * @param websocketPacket
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、null
	 * @author: tanyaowu
	 */
	@Override
	public Object onBytes(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception {
		String text = new String(bytes, "utf-8");
		log.info("收到byte消息:{},{}", bytes, text);
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		return buffer;
	}

	/** 
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public WsResponsePacket handler(ImPacket imPacket, ChannelContext channelContext)throws Exception {
		WsRequestPacket wsRequest = (WsRequestPacket)imPacket;
		return h(wsRequest, wsRequest.getBody(), wsRequest.getWsOpcode(), channelContext);
	}
	
	public WsResponsePacket h(WsRequestPacket wsRequest, byte[] bytes, Opcode opcode, ChannelContext channelContext) throws Exception {
		WsResponsePacket wsResponse = null;
		if (opcode == Opcode.TEXT) {
			if (bytes == null || bytes.length == 0) {
				Aio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			String text = new String(bytes, wsServerConfig.getCharset());
			Object retObj = this.onText(wsRequest, text, channelContext);
			String methodName = "onText";
			wsResponse = processRetObj(retObj, methodName, channelContext);
			return wsResponse;
		} else if (opcode == Opcode.BINARY) {
			if (bytes == null || bytes.length == 0) {
				Aio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			Object retObj = this.onBytes(wsRequest, bytes, channelContext);
			String methodName = "onBytes";
			wsResponse = processRetObj(retObj, methodName, channelContext);
			return wsResponse;
		} else if (opcode == Opcode.PING || opcode == Opcode.PONG) {
			log.error("收到" + opcode);
			return null;
		} else if (opcode == Opcode.CLOSE) {
			Object retObj = this.onClose(wsRequest, bytes, channelContext);
			String methodName = "onClose";
			wsResponse = processRetObj(retObj, methodName, channelContext);
			return wsResponse;
		} else {
			Aio.remove(channelContext, "错误的websocket包，错误的Opcode");
			return null;
		}
	}

	private WsResponsePacket processRetObj(Object obj, String methodName, ChannelContext channelContext) throws Exception {
		WsResponsePacket wsResponse = null;
		if (obj == null) {
			return null;
		} else {
			if (obj instanceof String) {
				String str = (String) obj;
				wsResponse = new WsResponsePacket();
				wsResponse.setBody(str.getBytes(wsServerConfig.getCharset()));
				wsResponse.setWsOpcode(Opcode.TEXT);
				return wsResponse;
			} else if (obj instanceof byte[]) {
				wsResponse = new WsResponsePacket();
				wsResponse.setBody((byte[]) obj);
				wsResponse.setWsOpcode(Opcode.BINARY);
				return wsResponse;
			} else if (obj instanceof WsResponsePacket) {
				return (WsResponsePacket) obj;
			} else if (obj instanceof ByteBuffer) {
				wsResponse = new WsResponsePacket();
				byte[] bs = ((ByteBuffer) obj).array();
				wsResponse.setBody(bs);
				wsResponse.setWsOpcode(Opcode.BINARY);
				return wsResponse;
			} else {
				log.error("{} {}.{}()方法，只允许返回byte[]、ByteBuffer、WebSocketResponsePacket或null，但是程序返回了{}", channelContext, this.getClass().getName(), methodName, obj.getClass().getName());
				return null;
			}
		}
		
	}
	@Override
	public Object onClose(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception {
		Aio.remove(channelContext, "receive close flag");
		return null;
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public WsMsgHandler(WsServerConfig wsServerConfig, String[] scanPackages) {
		this.setWsServerConfig(wsServerConfig);
		//this.routes = new Routes(scanPackages);
	}
	public WsMsgHandler() {
		this(new WsServerConfig(0), null);
	}

	/**
	 * @return the wsServerConfig
	 */
	public WsServerConfig getWsServerConfig() {
		return wsServerConfig;
	}

	/**
	 * @param wsServerConfig the wsServerConfig to set
	 */
	public void setWsServerConfig(WsServerConfig wsServerConfig) {
		this.wsServerConfig = wsServerConfig;
	}

}
