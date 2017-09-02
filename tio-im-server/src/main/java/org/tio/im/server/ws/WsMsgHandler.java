package org.tio.im.server.ws;

import java.nio.ByteBuffer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.im.common.Const;
import org.tio.im.common.ImStatus;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.server.util.Resps;
import org.tio.websocket.common.WsRequestPacket;
import org.tio.websocket.server.handler.IWsMsgHandler;

/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月1日 下午1:17:36
 */
public class WsMsgHandler implements IWsMsgHandler {
	
	private static Logger log = LoggerFactory.getLogger(WsMsgHandler.class);

	public WsMsgHandler() {
	}

	@Override
	public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
		return httpResponse;
	}

	@Override
	public Object onBytes(WsRequestPacket wsRequestPacket, byte[] bytes, ChannelContext channelContext) throws Exception {
		String ss = new String(bytes, "utf-8");
		log.info("收到byte消息:{},{}", bytes, ss);
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		return buffer;
	}

	@Override
	public Object onClose(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext channelContext) throws Exception {
		Aio.remove(channelContext, "receive close flag");
		return null;
	}

	@Override
	public Object onText(WsRequestPacket wsRequestPacket, String text, ChannelContext channelContext) throws Exception {
		Map<String,Object> resultMap = Resps.convertResPacket(wsRequestPacket.getBody(), channelContext);
		ChannelContext toChannleContext = (ChannelContext)resultMap.get(Const.CHANNEL);
		Packet packet = (Packet)resultMap.get(Const.PACKET);
		ImStatus status = (ImStatus)resultMap.get(Const.STATUS);
		if(toChannleContext != channelContext){//不发送给自己;
			Aio.send(toChannleContext, packet);
		}
		text = new String(ImUtils.toChatRespBody(status),HttpConst.CHARSET_NAME);
		return text;
	}
}
