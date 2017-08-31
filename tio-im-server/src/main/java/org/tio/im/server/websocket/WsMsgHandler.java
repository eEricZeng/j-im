package org.tio.im.server.websocket;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.websocket.WebSocketPacket.Opcode;
import org.tio.im.common.http.websocket.WebSocketRequestPacket;
import org.tio.im.common.http.websocket.WebSocketResponsePacket;
import org.tio.im.common.http.websocket.WebSocketSessionContext;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.ImBsHandlerIntf;
/**
 * @author tanyaowu 
 * 2017年6月28日 下午5:32:38
 */
public class WsMsgHandler implements IWsMsgHandler,ImBsHandlerIntf{
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
	public WebSocketResponsePacket handshake(HttpRequestPacket httpRequestPacket, HttpResponsePacket httpResponsePacket,ChannelContext channelContext) throws Exception {
		WebSocketSessionContext wsSessionContext = (WebSocketSessionContext)channelContext.getAttribute();
		WebSocketResponsePacket wsResponsePacket = new WebSocketResponsePacket();
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
	public Object onText(WebSocketRequestPacket websocketPacket, String text, ChannelContext channelContext) throws Exception {
		
		return text;
		/*String messageType = "/test/json"; // TODO 这里通过协议得到路径

		Method method = routes.pathMethodMap.get(messageType);
		if (method != null) {
			//			String[] paramnames = methodParamnameMap.get(method);
			Object bean = routes.methodBeanMap.get(method);

			Object obj = method.invoke(bean, websocketPacket, text, wsServerConfig, channelContext);
			return obj;

			//			if (obj instanceof WsResponsePacket) {
			//				return (WsResponsePacket)obj;
			//			} else {
			//				return null;
			//			}
		} else {
			log.error("没找到应对的处理方法");
			return null;
		}*/
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
	public Object onBytes(WebSocketRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception {
		byte[] bs1 = "收到消息:".getBytes(wsServerConfig.getCharset());
		ByteBuffer buffer = ByteBuffer.allocate(bs1.length + bytes.length);
		buffer.put(bs1);
		buffer.put(bytes);
		return buffer;
		/*String logstr = "业务不支持byte";
		log.error(logstr);
		Aio.remove(channelContext, logstr);
		return null;*/
	}

	/** 
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public WebSocketResponsePacket handler(ImPacket imPacket, ChannelContext channelContext)throws Exception {
		WebSocketRequestPacket webSocketRequestPacket = (WebSocketRequestPacket)imPacket;
		WebSocketResponsePacket wsResponsePacket = null;
		Opcode opcode = webSocketRequestPacket.getWsOpcode();
		byte[] bytes = webSocketRequestPacket.getBody();
		if (opcode == Opcode.TEXT) {
			if (bytes == null || bytes.length == 0) {
				Aio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			String text = new String(bytes, wsServerConfig.getCharset());
			Object retObj = onText(webSocketRequestPacket, text, channelContext);
			if (retObj != null) {
				if (retObj instanceof WebSocketResponsePacket) {
					Aio.send(channelContext, (WebSocketResponsePacket) retObj);
					return (WebSocketResponsePacket) retObj;
				} else if (retObj instanceof String) {
					String xx = (String) retObj;
					wsResponsePacket = new WebSocketResponsePacket();
					wsResponsePacket.setBody(xx.getBytes(wsServerConfig.getCharset()));
					wsResponsePacket.setWsOpcode(Opcode.TEXT);
					Aio.send(channelContext, wsResponsePacket);
					return wsResponsePacket;
				} else {
					log.error(this.getClass().getName() + "#onText()方法，只允许返回String或WsResponsePacket，但是程序返回了{}" + retObj.getClass().getName());
					return null;
				}

			} else {
				return null;
			}

		} else if (opcode == Opcode.BINARY) {
			if (bytes == null || bytes.length == 0) {
				Aio.remove(channelContext, "错误的websocket包，body为空");
				return null;
			}
			Object retObj = onBytes(webSocketRequestPacket, bytes, channelContext);
			if (retObj != null) {
				if (retObj instanceof WebSocketResponsePacket) {
					return (WebSocketResponsePacket) retObj;
				} else if (retObj instanceof byte[]) {
					wsResponsePacket = new WebSocketResponsePacket();
					wsResponsePacket.setBody((byte[]) retObj);
					wsResponsePacket.setWsOpcode(Opcode.BINARY);
					return wsResponsePacket;
				} else if (retObj instanceof ByteBuffer) {
					wsResponsePacket = new WebSocketResponsePacket();
					byte[] bs = ((ByteBuffer) retObj).array();
					wsResponsePacket.setBody(bs);
					wsResponsePacket.setWsOpcode(Opcode.BINARY);
					return wsResponsePacket;
				} else {
					log.error(this.getClass().getName() + "#onText()方法，只允许返回String或WsResponsePacket，但是程序返回了{}" + retObj.getClass().getName());
					return null;
				}
			} else {
				return null;
			}
		} else if (opcode == Opcode.PING || opcode == Opcode.PONG) {
			log.error("收到" + opcode);
			return null;
		} else if (opcode == Opcode.CLOSE) {
			onClose(webSocketRequestPacket, bytes, channelContext);
			return null;
		} else {
			Aio.remove(channelContext, "错误的websocket包，错误的Opcode");
			return null;
		}
	}
	
	@Override
	public Object onClose(WebSocketRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception {
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
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

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

	@Override
	public Command command() {
		
		return Command.COMMAND_WEBSOCKET_REQ;
	}

	

}
