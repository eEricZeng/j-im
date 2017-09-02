package org.tio.im.server.util;

import java.util.HashMap;
import java.util.Map;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.session.HttpSession;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacketType;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.ChatReqBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.tcp.TcpPacket;
import org.tio.im.common.tcp.TcpResponseEncoder;
import org.tio.im.common.utils.ImUtils;
import org.tio.websocket.common.Opcode;
import org.tio.websocket.common.WsResponsePacket;
import org.tio.websocket.common.WsSessionContext;
/**
 * @author tanyaowu 
 * 2017年6月29日 下午4:17:24
 */
public class Resps {
	//private static Logger log = LoggerFactory.getLogger(Resps.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Resps() {
	}

	/**
	 * 
		 * 功能描述：[转换不同协议响应包]
		 * 创建者：WChao 创建时间: 2017年8月29日 下午7:22:53
		 * @param packet
		 * @param fromChannelContext
		 * @return
	 * @throws Exception 
		 *
	 */
	public static Map<String,Object> convertResPacket(byte[] body, ChannelContext fromChannelContext) throws Exception{
		Map<String,Object> resultMap =  new HashMap<String,Object>();
		ChatReqBody chatReqBody = ImUtils.parseChatBody(body);
		if(chatReqBody != null){
			ChannelContext toChannelContext = Aio.getChannelContextByUserid(fromChannelContext.getGroupContext(),chatReqBody.getTo());
			if(toChannelContext == null){
				body = ImUtils.toChatRespBody(ImStatus.C0);
				Packet respPacket = convertPacket(body, fromChannelContext);
				resultMap.put(Const.CHANNEL,fromChannelContext);
				resultMap.put(Const.PACKET,respPacket);
				resultMap.put(Const.STATUS, ImStatus.C0);
				return resultMap;
			}else{
				try{
					resultMap.put(Const.CHANNEL,toChannelContext);
					body = chatReqBody.getContent().getBytes(HttpConst.CHARSET_NAME);
					Packet respPacket = convertPacket(body, toChannelContext);
					resultMap.put(Const.PACKET,respPacket);
					resultMap.put(Const.STATUS, ImStatus.C1);
				}catch(Exception e){
					
				}
			}
		}else{
			body = ImUtils.toChatRespBody(ImStatus.C2);
			Packet respPacket = convertPacket(body, fromChannelContext);
			resultMap.put(Const.CHANNEL,fromChannelContext);
			resultMap.put(Const.PACKET,respPacket);
			resultMap.put(Const.STATUS, ImStatus.C2);
			return resultMap;
		}
		return resultMap;
	}
	
	private static Packet convertPacket(byte[] body , ChannelContext channelContex){
		Object sessionContext = channelContex.getAttribute();
		Packet respPacket = null;
		if(sessionContext instanceof HttpSession){//转HTTP协议响应包;
			HttpRequest request = (HttpRequest)channelContex.getAttribute(Const.HTTP_REQUEST);
			HttpResponse response = new HttpResponse(request,request.getHttpConfig());
			response.setBody(body, request);
			respPacket = response;
		}else if(sessionContext instanceof ImSessionContext){//转TCP协议响应包;
			TcpPacket tcpPacket = new TcpPacket(Command.COMMAND_CHAT_RESP, body,ImPacketType.TCP);
			TcpResponseEncoder.encode(tcpPacket, channelContex.getGroupContext(), channelContex);
			respPacket = tcpPacket;
		}else if(sessionContext instanceof WsSessionContext){//转ws协议响应包;
			WsResponsePacket wsResponsePacket = new WsResponsePacket();
			wsResponsePacket.setBody(body);
			wsResponsePacket.setWsOpcode(Opcode.TEXT);
			respPacket =wsResponsePacket;
		}
		return respPacket;
	}
	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
