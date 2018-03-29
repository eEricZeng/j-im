/**
 * 
 */
package org.tio.im.server.command.handler;

import java.util.List;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImConfig;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;
import org.tio.im.common.packets.UserReqBody;
import org.tio.im.common.utils.ImKit;
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
		List<User> clients = null;
		RespBody resPacket = null;
		if(userReqBody.getType() == null || 0 == userReqBody.getType()){
			clients = ImAio.getUser(ImConfig.groupContext, userReqBody.getUserid());
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10003);
		}else if(1 == userReqBody.getType()){
			clients = ImAio.getAllOnlineUser(ImConfig.groupContext);
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10005);
		}else if(2 == userReqBody.getType()){
			clients = ImAio.getAllUser(ImConfig.groupContext);
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10006);
		}
		if(clients == null)
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10004), channelContext);
		resPacket.setData(clients);
		return ImKit.ConvertRespPacket(resPacket, channelContext);
	}

}
