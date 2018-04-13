package org.jim.common.ws;

import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;
import org.jim.common.ws.WsRequestPacket;
/**
 * 
 * @author WChao 
 * 2017年7月30日 上午9:34:59
 */
public interface IWsMsgHandler
{
	/**
	 * 
	 * @param packet
	 * @param channelContext
	 * @return
	 *
	 * @author: WChao
	 * 2016年11月18日 下午1:08:45
	 *
	 */
	public ImPacket handler(ImPacket packet, ChannelContext channelContext)  throws Exception;
	/**
	 * @param websocketPacket
	 * @param text
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author: WChao
	 */
	Object onText(WsRequestPacket wsPacket, String text, ChannelContext channelContext) throws Exception;
	
	/**
	 * 
	 * @param websocketPacket
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author: WChao
	 */
	Object onClose(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception;

	/**
	 * 
	 * @param websocketPacket
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponsePacket、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author: WChao
	 */
	Object onBytes(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception;
}
