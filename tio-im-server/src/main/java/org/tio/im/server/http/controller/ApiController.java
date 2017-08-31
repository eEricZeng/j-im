/**
 * 
 */
package org.tio.im.server.http.controller;

import java.util.Map;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.server.http.HttpServerConfig;
import org.tio.im.server.http.annotation.RequestPath;
import org.tio.im.server.util.Resps;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月8日 上午9:08:48
 */
@RequestPath(value = "/api")
public class ApiController {
	
	@RequestPath(value = "/message/send")
	public HttpResponsePacket json(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext channelContext)throws Exception {
		HttpResponsePacket httpResponsePacket = new HttpResponsePacket(httpRequestPacket);
		Map<String,Object> resultMap = Resps.convertResPacket(httpRequestPacket, channelContext);
		if(resultMap != null){
			ChannelContext toChannleContext = (ChannelContext)resultMap.get(Const.CHANNEL);
			ImPacket imPacket = (ImPacket)resultMap.get(Const.PACKET);
			if(toChannleContext == channelContext){//发送给自己，扯淡;
				httpResponsePacket = (HttpResponsePacket)imPacket;
			}else{
				Aio.send(toChannleContext, imPacket);
				httpResponsePacket.setBody(Resps.chatRespBody((ImStatus)imPacket.getStatus()));
			}
		}
		return httpResponsePacket;
	}
}
