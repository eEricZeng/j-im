/**
 * 
 */
package org.tio.im.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.http.session.HttpSession;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.tcp.TcpPacket;
import org.tio.im.common.tcp.TcpServerEncoder;
import org.tio.im.common.tcp.TcpSessionContext;
import org.tio.im.common.ws.Opcode;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.common.ws.WsSessionContext;
/**
 * @author WChao
 *
 */
public class ImKit {
	
	private static Logger logger = LoggerFactory.getLogger(ImKit.class);
	/**
	 * 
		 * 功能描述：[转换不同协议响应包]
		 * 创建者：WChao 创建时间: 2017年9月21日 下午3:21:54
		 * @param body
		 * @param channelContext
		 * @return
		 *
	 */
	public static ImPacket ConvertRespPacket(RespBody respBody, ChannelContext channelContext){
		ImPacket respPacket = null;
		if(respBody == null)
			return respPacket;
		byte[] body;
		try {
			body = respBody.toString().getBytes(HttpConst.CHARSET_NAME);
			respPacket = ConvertRespPacket(body,respBody.getCommand(), channelContext);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return respPacket;
	}
	/**
	 * 
		 * 功能描述：[转换不同协议响应包]
		 * 创建者：WChao 创建时间: 2017年9月21日 下午3:21:54
		 * @param body
		 * @param channelContext
		 * @return
		 *
	 */
	public static ImPacket ConvertRespPacket(byte[] body,Command command, ChannelContext channelContext){
		Object sessionContext = channelContext.getAttribute();
		ImPacket respPacket = null;
		if(sessionContext instanceof HttpSession){//转HTTP协议响应包;
			HttpRequest request = (HttpRequest)channelContext.getAttribute(Const.HTTP_REQUEST);
			HttpResponse response = new HttpResponse(request,request.getHttpConfig());
			response.setBody(body, request);
			respPacket = response;
		}else if(sessionContext instanceof TcpSessionContext){//转TCP协议响应包;
			TcpPacket tcpPacket = new TcpPacket(command,body);
			TcpServerEncoder.encode(tcpPacket, channelContext.getGroupContext(), channelContext);
			respPacket = tcpPacket;
		}else if(sessionContext instanceof WsSessionContext){//转ws协议响应包;
			WsResponsePacket wsResponsePacket = new WsResponsePacket();
			wsResponsePacket.setBody(body);
			wsResponsePacket.setWsOpcode(Opcode.TEXT);
			respPacket = wsResponsePacket;
		}
		respPacket.setCommand(command);
		return respPacket;
	}
	/**
	 * 获取所属终端协议;
	 * @param channelContext
	 * @return
	 */
	public static String getTerminal(ChannelContext channelContext){
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext instanceof HttpSession){//HTTP协议;
			return Protocol.HTTP;
		}else if(sessionContext instanceof TcpSessionContext){//TCP协议;
			return Protocol.TCP;
		}else if(sessionContext instanceof WsSessionContext){//ws协议;
			return Protocol.WEBSOCKET;
		}
		return "";
	}
	/**
	 * 格式化状态码消息响应体;
	 * @param status
	 * @return
	 */
	public static byte[] toImStatusBody(ImStatus status){
		return JsonKit.toJsonBytes(new RespBody().setCode(status.getCode()).setMsg(status.getDescription()+" "+status.getText()));
	}
}
