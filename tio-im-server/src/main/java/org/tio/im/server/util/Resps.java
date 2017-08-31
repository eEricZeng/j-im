package org.tio.im.server.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImPacketType;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.HttpResponseStatus;
import org.tio.im.common.http.MimeType;
import org.tio.im.common.http.websocket.WebSocketPacket.Opcode;
import org.tio.im.common.http.websocket.WebSocketRequestPacket;
import org.tio.im.common.packets.ChatReqBody;
import org.tio.im.common.packets.ChatRespBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.tcp.TcpPacket;
import org.tio.im.common.tcp.TcpResponseEncoder;
import org.tio.im.server.websocket.WebSocketServerHandler;
import org.tio.im.server.websocket.WsMsgHandler;
import org.tio.im.server.websocket.WsServerConfig;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.io.FileUtil;

/**
 * @author tanyaowu 
 * 2017年6月29日 下午4:17:24
 */
public class Resps {
	private static Logger log = LoggerFactory.getLogger(Resps.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Resps() {
	}

	/**
	 * Content-Type: text/html; charset=utf-8
	 * @param httpRequestPacket
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket html(HttpRequestPacket httpRequestPacket, String bodyString, String charset) {
		HttpResponsePacket ret = string(httpRequestPacket, bodyString, charset, MimeType.TEXT_HTML_HTML.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * 根据文件创建响应
	 * @param httpRequestPacket
	 * @param fileOnServer
	 * @return
	 * @throws IOException
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket file(HttpRequestPacket httpRequestPacket, File fileOnServer) throws IOException {
		Date lastModified = FileUtil.lastModifiedTime(fileOnServer);
		
		String If_Modified_Since = httpRequestPacket.getHeader(HttpConst.RequestHeaderKey.If_Modified_Since);//If-Modified-Since
		if (StringUtils.isNoneBlank(If_Modified_Since)) {
			Long If_Modified_Since_Date = null;
			try {
//				If_Modified_Since_Date = DatePattern.NORM_DATETIME_MS_FORMAT.parse(If_Modified_Since);
				If_Modified_Since_Date = Long.parseLong(If_Modified_Since);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			
			if (If_Modified_Since_Date != null) {
				long lastModifiedTime = Long.MAX_VALUE;
				try {
					//此处这样写是为了保持粒度一致，否则可能会判断失误
					lastModifiedTime = lastModified.getTime();
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
//				long If_Modified_Since_Date_Time = If_Modified_Since_Date.getTime();
				if (lastModifiedTime <= If_Modified_Since_Date) {
					HttpResponsePacket ret = new HttpResponsePacket(httpRequestPacket);
					ret.setStatus(HttpResponseStatus.C304);
					return ret;
				}
			}
		}
		
		byte[] bodyBytes = FileUtil.readBytes(fileOnServer);
		String filename = fileOnServer.getName();
		HttpResponsePacket ret = file(httpRequestPacket, bodyBytes, filename);
		ret.addHeader(HttpConst.ResponseHeaderKey.Last_Modified,  lastModified.getTime() + "");
		return ret;
	}

	/**
	 * 根据文件创建响应
	 * @param httpRequestPacket
	 * @param bodyBytes
	 * @param filename
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket file(HttpRequestPacket httpRequestPacket, byte[] bodyBytes, String filename) {
		HttpResponsePacket ret = new HttpResponsePacket(httpRequestPacket);
		ret.setBody(bodyBytes, httpRequestPacket);

		String mimeTypeStr = null;
		String extension = FilenameUtils.getExtension(filename);
		if (StringUtils.isNoneBlank(extension)) {
			MimeType mimeType = MimeType.fromExtension(extension);
			if (mimeType != null) {
				mimeTypeStr = mimeType.getType();
			} else {
				mimeTypeStr = "application/octet-stream";
			}
		}
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, mimeTypeStr);
		//		ret.addHeader(HttpConst.ResponseHeaderKey.Content_disposition, "attachment;filename=\"" + filename + "\"");
		return ret;
	}

	/**
	 * Content-Type: application/json; charset=utf-8
	 * @param httpRequestPacket
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket json(HttpRequestPacket httpRequestPacket, String bodyString, String charset) {
		HttpResponsePacket ret = string(httpRequestPacket, bodyString, charset, MimeType.TEXT_PLAIN_JSON.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: text/css; charset=utf-8
	 * @param httpRequestPacket
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket css(HttpRequestPacket httpRequestPacket, String bodyString, String charset) {
		HttpResponsePacket ret = string(httpRequestPacket, bodyString, charset, MimeType.TEXT_CSS_CSS.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: application/javascript; charset=utf-8
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket js(HttpRequestPacket httpRequestPacket, String bodyString, String charset) {
		HttpResponsePacket ret = string(httpRequestPacket, bodyString, charset, MimeType.APPLICATION_JAVASCRIPT_JS.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: text/plain; charset=utf-8
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket txt(HttpRequestPacket httpRequestPacket, String bodyString, String charset) {
		HttpResponsePacket ret = string(httpRequestPacket, bodyString, charset, MimeType.TEXT_PLAIN_TXT.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * 创建字符串输出
	 * @param bodyString
	 * @param charset
	 * @param Content_Type
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponsePacket string(HttpRequestPacket httpRequestPacket, String bodyString, String charset, String Content_Type) {
		HttpResponsePacket ret = new HttpResponsePacket(httpRequestPacket);
		if (bodyString != null) {
			try {
				ret.setBody(bodyString.getBytes(charset), httpRequestPacket);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, Content_Type);
		return ret;
	}
	
	/**
	 * 重定向
	 * @param httpRequestPacket
	 * @param path
	 * @return
	 * @author: tanyaowu
	 */
	//	　　302 （307）：与响应头location 结合完成页面重新跳转。
	public static HttpResponsePacket redirect(HttpRequestPacket httpRequestPacket, String path) {
		HttpResponsePacket ret = new HttpResponsePacket(httpRequestPacket);
		ret.setStatus(HttpResponseStatus.C302);
		ret.addHeader(HttpConst.ResponseHeaderKey.Location, path);
		return ret;
	}
	
	/**
	 * 格式化状态码消息响应体;
	 * @param status
	 * @return
	 */
	public static byte[] chatRespBody(ImStatus status){
		return JSONObject.toJSONBytes(new ChatRespBody().setErrorCode(status.getCode()).setErrorMsg(status.getDescription()+" "+status.getText()));
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	public static ChatReqBody isChatBody(ImPacket packet){
		ChatReqBody chatReqBody = null;
		try{
			String text = new String(packet.getBody(),HttpConst.CHARSET_NAME);
		    chatReqBody = JSONObject.parseObject(text,ChatReqBody.class);
			if(chatReqBody != null)
				return chatReqBody;
		}catch(Exception e){
			
		}
		return chatReqBody;
	}
	/**
	 * 
		 * 功能描述：[转换不同协议响应包]
		 * 创建者：WChao 创建时间: 2017年8月29日 下午7:22:53
		 * @param packet
		 * @param fromChannelContext
		 * @return
		 *
	 */
	public static Map<String,Object> convertResPacket(ImPacket packet , ChannelContext fromChannelContext){
		Map<String,Object> resultMap =  new HashMap<String,Object>();
		ChatReqBody chatReqBody = isChatBody(packet);
		byte[] body = null;
		ImSessionContext fromSessionContext = (ImSessionContext)fromChannelContext.getAttribute();
		ImPacketType fromImPacketType = fromSessionContext.getPacketType();
		if(chatReqBody != null){
			ChannelContext toChannelContext = Aio.getChannelContextByUserid(fromChannelContext.getGroupContext(),chatReqBody.getTo());
			if(toChannelContext == null){
				body = chatRespBody(ImStatus.C0);
				ImPacket respPacket = convertPacket(fromImPacketType, body, fromChannelContext);
				if(respPacket != null){
					respPacket.setStatus(ImStatus.C0);
				}
				resultMap.put(Const.CHANNEL,fromChannelContext);
				resultMap.put(Const.PACKET,respPacket);
				return resultMap;
			}else{
				try{
					resultMap.put(Const.CHANNEL,toChannelContext);
					ImSessionContext toSessionContext = (ImSessionContext)toChannelContext.getAttribute();
					ImPacketType toImPacketType = toSessionContext.getPacketType();
					body = chatReqBody.getContent().getBytes(HttpConst.CHARSET_NAME);
					ImPacket respPacket = convertPacket(toImPacketType, body, toChannelContext);
					if(respPacket != null){
						respPacket.setStatus(ImStatus.C1);
					}
					resultMap.put(Const.PACKET,respPacket);
				}catch(Exception e){
					
				}
			}
		}else{
			body = chatRespBody(ImStatus.C2);
			ImPacket respPacket = convertPacket(fromImPacketType, body, fromChannelContext);
			if(respPacket != null){
				respPacket.setStatus(ImStatus.C2);
			}
			resultMap.put(Const.CHANNEL,fromChannelContext);
			resultMap.put(Const.PACKET,respPacket);
			return resultMap;
		}
		return resultMap;
	}
	
	private static ImPacket convertPacket(ImPacketType packetType,byte[] body , ChannelContext channelContex){
		ImPacket imPacket = null;
		if(packetType == ImPacketType.HTTP){//转HTTP协议响应包;
			HttpRequestPacket httpRequestPacket = new HttpRequestPacket();
			HttpResponsePacket httpResponsePacket = new HttpResponsePacket(httpRequestPacket);
			httpResponsePacket.setBody(body);
			imPacket = httpResponsePacket;
		}else if(packetType == ImPacketType.TCP){//转TCP协议响应包;
			TcpPacket tcpPacket = new TcpPacket(Command.COMMAND_CHAT_RESP, body,ImPacketType.TCP);
			TcpResponseEncoder.encode(tcpPacket, channelContex.getGroupContext(), channelContex);
			imPacket = tcpPacket;
		}else if(packetType == ImPacketType.WS){//转ws协议响应包;
			WebSocketRequestPacket wsRequestPacket = new WebSocketRequestPacket();
			wsRequestPacket.setBody(body);
			wsRequestPacket.setWsOpcode(Opcode.TEXT);
			try {
				imPacket = new WebSocketServerHandler(new WsServerConfig(0),new WsMsgHandler()).h(wsRequestPacket, wsRequestPacket.getBody(), wsRequestPacket.getWsOpcode(), channelContex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imPacket;
	}
	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
