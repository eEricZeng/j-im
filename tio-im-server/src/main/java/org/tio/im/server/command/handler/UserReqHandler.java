/**
 * 
 */
package org.tio.im.server.command.handler;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.UserReqBody;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.AbCmdHandler;

import com.alibaba.fastjson.JSONObject;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月18日 下午4:08:47
 */
public class UserReqHandler extends AbCmdHandler{

	@Override
	public Command command() {
		return Command.COMMAND_GET_USER_REQ;
	}

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		UserReqBody userReqBody = JSONObject.parseObject(packet.getBody(),UserReqBody.class);
		Object clients = null;
		if(userReqBody.getType() == null || 0 == userReqBody.getType()){
			clients = ImAio.getUser(channelContext.getGroupContext(), userReqBody.getUserid());
		}else if(1 == userReqBody.getType()){
			clients = ImAio.getAllOnlineUser(channelContext.getGroupContext());
		}else if(2 == userReqBody.getType()){
			clients = ImAio.getAllUser(channelContext.getGroupContext());
		}
		if(clients == null)
			return Resps.convertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C101), channelContext);
		RespBody resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C100);
		resPacket.setData(JSONObject.toJSONString(clients));
		return Resps.convertRespPacket(resPacket, channelContext);
	}

}
