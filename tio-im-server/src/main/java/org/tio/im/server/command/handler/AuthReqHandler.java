package org.tio.im.server.command.handler;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.im.common.Const;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.AuthReqBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.utils.ImKit;
import org.tio.im.server.command.AbCmdHandler;

import com.alibaba.fastjson.JSONObject;
/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月13日 下午1:39:35
 */
public class AuthReqHandler extends AbCmdHandler
{
	
	private Logger logger = Logger.getLogger(AuthReqHandler.class);
	
	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		if (packet.getBody() == null) {
			RespBody respBody = new RespBody(Command.COMMAND_AUTH_RESP,ImStatus.C10010);
			return ImKit.ConvertRespPacket(respBody, channelContext);
		}
		AuthReqBody authReqBody = JSONObject.parseObject(packet.getBody(), AuthReqBody.class);
		String token = authReqBody.getToken() == null ? "" : authReqBody.getToken();
		String data = token +  Const.authkey;
		logger.info(data);
		authReqBody.setToken(data);
		RespBody respBody = new RespBody(Command.COMMAND_AUTH_RESP,ImStatus.C10009).setData(JSONObject.toJSONString(authReqBody));
		return ImKit.ConvertRespPacket(respBody, channelContext);
	}

	@Override
	public Command command() {
		return Command.COMMAND_AUTH_REQ;
	}
}
