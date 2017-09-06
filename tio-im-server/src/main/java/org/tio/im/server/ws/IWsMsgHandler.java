package org.tio.im.server.ws;

import org.tio.core.ChannelContext;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.ws.WsRequestPacket;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.server.command.ImBsHandlerIntf;

/**
 * 
 * @author tanyaowu 
 * 2017年7月30日 上午9:34:59
 */
public interface IWsMsgHandler extends ImBsHandlerIntf
{
	/**
	 * 对httpResponsePacket参数进行补充并返回，如果返回null表示不想和对方建立连接，框架会断开连接，如果返回非null，框架会把这个对象发送给对方
	 * @param httpRequestPacket
	 * @param httpResponsePacket
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public WsResponsePacket handshake(HttpRequest httpRequestPacket, HttpResponse httpResponsePacket, ChannelContext channelContext)  throws Exception;

	/**
	 * @param websocketPacket
	 * @param text
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author: tanyaowu
	 */
	Object onText(WsRequestPacket wsPacket, String text, ChannelContext channelContext) throws Exception;
	
	/**
	 * 
	 * @param websocketPacket
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author: tanyaowu
	 */
	Object onClose(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception;

	/**
	 * 
	 * @param websocketPacket
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author: tanyaowu
	 */
	Object onBytes(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception;
}
