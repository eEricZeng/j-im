/**
 * 
 */
package org.tio.im.server.http.controller;

import java.util.Map;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.server.annotation.RequestPath;
import org.tio.im.common.Const;
import org.tio.im.common.ImStatus;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.server.util.Resps;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月8日 上午9:08:48
 */
@RequestPath(value = "/api")
public class ApiController {
	
	@RequestPath(value = "/message/send")
	public HttpResponse json(HttpRequest request, HttpConfig httpConfig, ChannelContext channelContext)throws Exception {
		HttpResponse response = new HttpResponse(request,httpConfig);
		Map<String,Object> resultMap = Resps.convertResPacket(request.getBody(), channelContext);
		if(resultMap != null){
			ChannelContext toChannleContext = (ChannelContext)resultMap.get(Const.CHANNEL);
			Packet packet = (Packet)resultMap.get(Const.PACKET);
			ImStatus status = (ImStatus)resultMap.get(Const.STATUS);
			if(toChannleContext == channelContext){//发送给自己，扯淡;
				response = (HttpResponse)packet;
			}else{
				Aio.send(toChannleContext, packet);
				response.setBody(ImUtils.toChatRespBody(status),request);
			}
		}
		return response;
	}
}
