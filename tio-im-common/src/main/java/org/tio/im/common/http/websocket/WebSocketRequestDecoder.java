package org.tio.im.common.http.websocket;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.Cookie;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.HttpResponseStatus;
import org.tio.im.common.http.websocket.WebSocketPacket.Opcode;
import org.tio.im.common.packets.Command;
import org.tio.im.common.utils.BASE64Util;
import org.tio.im.common.utils.SHA1Util;
/**
 * 参考了baseio: https://git.oschina.net/generallycloud/baseio
 * com.generallycloud.nio.codec.http11.future.WebSocketReadFutureImpl
 * @author tanyaowu 
 *
 */
public class WebSocketRequestDecoder {
	private static Logger log = LoggerFactory.getLogger(WebSocketRequestDecoder.class);

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public WebSocketRequestDecoder() {

	}

	//	public static final int MAX_HEADER_LENGTH = 20480;

	public static WebSocketRequestPacket decode(ByteBuffer buf, ChannelContext channelContext) throws AioDecodeException {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		List<byte[]> lastParts = imSessionContext.getLastParts();

		//第一阶段解析
		int initPosition = buf.position();
		int readableLength = buf.limit() - initPosition;

		int headLength = WebSocketRequestPacket.MINIMUM_HEADER_LENGTH;

		if (readableLength < headLength) {
			return null;
		}

		byte first = buf.get();
		//		int b = first & 0xFF; //转换成32位
		boolean fin = (first & 0x80) > 0; //得到第8位 10000000>0
		@SuppressWarnings("unused")
		int rsv = (first & 0x70) >>> 4;//得到5、6、7 为01110000 然后右移四位为00000111
		byte opCodeByte = (byte) (first & 0x0F);//后四位为opCode 00001111
		Opcode opcode = Opcode.valueOf(opCodeByte);
		if (opcode == Opcode.CLOSE) {
//			Aio.remove(channelContext, "收到opcode:" + opcode);
//			return null;
		}
		if (!fin) {
			log.error("{} 暂时不支持fin为false的请求", channelContext);
			Aio.remove(channelContext, "暂时不支持fin为false的请求");
			return null;
			//下面这段代码不要删除，以后若支持fin，则需要的
			//			if (lastParts == null) {
			//				lastParts = new ArrayList<>();
			//				imSessionContext.setLastParts(lastParts);
			//			}
		} else {
			imSessionContext.setLastParts(null);
		}

		byte second = buf.get(); //向后读取一个字节
		boolean hasMask = ((second & 0xFF) >> 7) == 1; //用于标识PayloadData是否经过掩码处理。如果是1，Masking-key域的数据即是掩码密钥，用于解码PayloadData。客户端发出的数据帧需要进行掩码处理，所以此位是1。

		// Client data must be masked
		if (!hasMask) { //第9为为mask,必须为1
			//throw new AioDecodeException("websocket client data must be masked");
		} else {
			headLength += 4;
		}
		int payloadLength = second & 0x7F; //读取后7位  Payload legth，如果<126则payloadLength 

		byte[] mask = null;
		if (payloadLength == 126) { //为126读2个字节，后两个字节为payloadLength
			headLength += 2;
			if (readableLength < headLength) {
				return null;
			}
			payloadLength = ByteBufferUtils.readUB2WithBigEdian(buf);
			log.info("{} payloadLengthFlag: 126，payloadLength {}", channelContext, payloadLength);

		} else if (payloadLength == 127) { //127读8个字节,后8个字节为payloadLength
			headLength += 8;
			if (readableLength < headLength) {
				return null;
			}

			payloadLength = (int) buf.getLong();
			log.info("{} payloadLengthFlag: 127，payloadLength {}", channelContext, payloadLength);
		}

		if (payloadLength < 0 || payloadLength > WebSocketRequestPacket.MAX_BODY_LENGTH) {
			throw new AioDecodeException("body length(" + payloadLength + ") is not right");
		}

		if (readableLength < (headLength + payloadLength)) {
			return null;
		}

		if (hasMask) {
			mask = ByteBufferUtils.readBytes(buf, 4);
		}

		//第二阶段解析		
		WebSocketRequestPacket websocketPacket = new WebSocketRequestPacket();
		websocketPacket.setWsEof(fin);
		websocketPacket.setWsHasMask(hasMask);
		websocketPacket.setWsMask(mask);
		websocketPacket.setWsOpcode(opcode);
		websocketPacket.setWsBodyLength(payloadLength);

		if (payloadLength == 0) {
			return websocketPacket;
		}

		byte[] array = ByteBufferUtils.readBytes(buf, (int) payloadLength);
		if (hasMask) {
			for (int i = 0; i < array.length; i++) {
				array[i] = (byte) (array[i] ^ mask[i % 4]);
			}
		}

		if (!fin) {
			//lastParts.add(array);
			log.error("payloadLength {}, lastParts size {}, array length {}", payloadLength, lastParts.size(), array.length);
			return websocketPacket;
		} else {
			int allLength = array.length;
			if (lastParts != null) {
				for (byte[] part : lastParts) {
					allLength += part.length;
				}
				byte[] allByte = new byte[allLength];

				int offset = 0;
				for (byte[] part : lastParts) {
					System.arraycopy(part, 0, allByte, offset, part.length);
					offset += part.length;
				}
				System.arraycopy(array, 0, allByte, offset, array.length);
				//offset += array.length;

				array = allByte;
			}

			websocketPacket.setBody(array);
			String text = null;
			if (opcode == Opcode.BINARY) {

			} else {
				try {
					text = new String(array, WebSocketRequestPacket.CHARSET_NAME);
					websocketPacket.setWsBodyText(text);
				} catch (UnsupportedEncodingException e) {
					log.error(e.toString(), e);
				}
			}
		}
		return websocketPacket;

	}

	/**
	 * 
		 * 功能描述：[升级更新包协议]
		 * 创建者：WChao 创建时间: 2017年7月27日 下午5:08:48
		 * @param packet
		 * @return
		 *
	 */
	public static WebSocketRequestPacket updateRequestPacketProtocol(Packet packet,ChannelContext channelContext) {
		HttpRequestPacket httpRequestPacket = (HttpRequestPacket)packet;
		Map<String, String> headers = httpRequestPacket.getHeaders();

		String Sec_WebSocket_Key = headers.get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key);

		if (StringUtils.isNotBlank(Sec_WebSocket_Key)) {
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			HttpResponsePacket httpResponsePacket = updateWebSocketProtocol(httpRequestPacket,channelContext);
			
			imSessionContext.setHandshakeRequestPacket(httpRequestPacket);
			imSessionContext.setHandshakeResponsePacket(httpResponsePacket);
			imSessionContext.setWebsocket(true);
			
			WebSocketRequestPacket webSocketRequestPacket = new WebSocketRequestPacket(httpRequestPacket);
			webSocketRequestPacket.setCommand(Command.COMMAND_HANDSHAKE_REQ);
			webSocketRequestPacket.setHandShake(true);
			
			return webSocketRequestPacket;
		}
		return null;
	}
	/**
	 * 升级更新WebSocket协议;
	 * @param httpRequestPacket
	 * @param channelContext
	 * @return
	 */
	private static HttpResponsePacket updateWebSocketProtocol(HttpRequestPacket httpRequestPacket,ChannelContext channelContext){
		
		Map<String, String> headers = httpRequestPacket.getHeaders();

		String Sec_WebSocket_Key = headers.get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key);

		if (StringUtils.isNotBlank(Sec_WebSocket_Key)) {
			String Sec_WebSocket_Key_Magic = Sec_WebSocket_Key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
			byte[] key_array = SHA1Util.SHA1(Sec_WebSocket_Key_Magic);
			String acceptKey = BASE64Util.byteArrayToBase64(key_array);
			HttpResponsePacket httpResponsePacket = new HttpResponsePacket(httpRequestPacket);

			httpResponsePacket.setStatus(HttpResponseStatus.C101);

			Map<String, String> respHeaders = new HashMap<>();
			respHeaders.put(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.Upgrade);
			respHeaders.put(HttpConst.ResponseHeaderKey.Upgrade, "WebSocket");
			respHeaders.put(HttpConst.ResponseHeaderKey.Sec_WebSocket_Accept, acceptKey);
			httpResponsePacket.setHeaders(respHeaders);
			
			return httpResponsePacket;
		}
		return null;
	}
	
	public static WebSocketResponsePacket updateResponsePacketProtocol(HttpRequestPacket httpRequestPacket,ChannelContext channelContext){
		Map<String, String> headers = httpRequestPacket.getHeaders();
		String Sec_WebSocket_Key = headers.get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key);
		if (StringUtils.isNotBlank(Sec_WebSocket_Key)) {
			HttpResponsePacket httpResponsePacket = updateWebSocketProtocol(httpRequestPacket, channelContext);
			Cookie sessionCookie = httpRequestPacket.getCookieByName(Protocol.COOKIE_NAME_FOR_SESSION);
			if (sessionCookie == null) {
				sessionCookie = new Cookie();
				sessionCookie.setDomain(StringUtils.split(headers.get(HttpConst.RequestHeaderKey.Host), ":")[0]);
				sessionCookie.setName(Protocol.COOKIE_NAME_FOR_SESSION);
				sessionCookie.setValue(channelContext.getId());
				httpResponsePacket.addCookie(sessionCookie);
			} else {
				//
			}
			if(sessionCookie != null){
				ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
				imSessionContext.setToken(sessionCookie.getValue());
			}
			return new WebSocketResponsePacket(httpResponsePacket);
		}
		return null;
	}
	public static enum Step {
		header, remain_header, data,
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * 
	 */
	public static void main(String[] args) {

	}

}
