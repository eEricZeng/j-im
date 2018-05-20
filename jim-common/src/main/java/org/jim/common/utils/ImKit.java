/**
 * 
 */
package org.jim.common.utils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jim.common.Const;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.http.HttpConst;
import org.jim.common.http.HttpProtocol;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.protocol.AbProtocol;
import org.jim.common.protocol.IConvertProtocolPacket;
import org.jim.common.protocol.IProtocol;
import org.jim.common.tcp.TcpProtocol;
import org.jim.common.ws.WsProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
/**
 * IM工具类;
 * @author WChao
 *
 */
public class ImKit {
	
	private static Logger logger = LoggerFactory.getLogger(ImKit.class);
	private static Map<String,AbProtocol> protocols = new HashMap<String,AbProtocol>();
	
	static{
		WsProtocol wsProtocol = new WsProtocol();
		TcpProtocol tcpProtocol = new TcpProtocol();
		HttpProtocol httpProtocol = new HttpProtocol();
		protocols.put(wsProtocol.name(),wsProtocol);
		protocols.put(tcpProtocol.name(),tcpProtocol);
		protocols.put(httpProtocol.name(),httpProtocol);
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
		ImPacket respPacket = null;
		IConvertProtocolPacket convertor = (IConvertProtocolPacket)channelContext.getAttribute(Const.CONVERTOR);
		if(convertor != null){
			return convertor.RespPacket(body, command, channelContext);
		}
		for(Entry<String,AbProtocol> entry : protocols.entrySet()){
			AbProtocol protocol = entry.getValue();
			IConvertProtocolPacket convertorObj = protocol.getConvertor();
			respPacket = convertorObj.RespPacket(body, command, channelContext);
			if(respPacket != null){
				channelContext.setAttribute(Const.CONVERTOR, convertorObj);
				return respPacket;
			}
		}
		return respPacket;
	}
	
	public static ImPacket ConvertRespPacket(ImPacket imPacket,Command command, ChannelContext channelContext){
		ImPacket respPacket = ConvertRespPacket(imPacket.getBody(), command, channelContext);
		if(respPacket == null){
			for(Entry<String,AbProtocol> entry : protocols.entrySet()){
				AbProtocol protocol = entry.getValue();
				try{
					boolean isProtocol = protocol.isProtocol(imPacket,channelContext);
					if(isProtocol){
						IConvertProtocolPacket convertorObj = protocol.getConvertor();
						respPacket = convertorObj.RespPacket(imPacket.getBody(), command, channelContext);
						if(respPacket != null){
							channelContext.setAttribute(Const.CONVERTOR, convertorObj);
							return respPacket;
						}
					}
				}catch(Throwable e){
					logger.error(e.toString());
				}
			}
		}
		return respPacket;
	}
	/**
	 * 获取所属终端协议;
	 * @param byteBuffer
	 * @param channelContext
	 */
	public static IProtocol protocol(ByteBuffer byteBuffer , ChannelContext channelContext){
		for(Entry<String,AbProtocol> entry : protocols.entrySet()){
			AbProtocol protocol = entry.getValue();
			try {
				boolean isPrototol = protocol.isProtocol(byteBuffer, channelContext);
				if(isPrototol){
					return protocol;
				}
			} catch (Throwable e) {
				logger.error(e.toString(),e);
			}
		}
		return null;
	}
	/**
	 * 格式化状态码消息响应体;
	 * @param status
	 * @return
	 */
	public static byte[] toImStatusBody(ImStatus status){
		return JsonKit.toJsonBytes(new RespBody().setCode(status.getCode()).setMsg(status.getDescription()+" "+status.getText()));
	}
	/**
	 * 获取所有协议判断器，目前内置(HttpProtocol、WebsocketProtocol、HttpProtocol)
	 * @return
	 */
	public static Map<String, AbProtocol> getProtocols() {
		return protocols;
	}
}
