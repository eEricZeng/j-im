/**
 * 
 */
package org.jim.server.command.handler;

import java.util.List;

import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.packets.UserReqBody;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbCmdHandler;
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
		UserReqBody userReqBody = JsonKit.toBean(packet.getBody(),UserReqBody.class);
		List<User> clients = null;
		RespBody resPacket = null;
		if(userReqBody.getType() == null || 0 == userReqBody.getType()){
			clients = ImAio.getUser(userReqBody.getUserid());
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10003);
		}else if(1 == userReqBody.getType()){
			clients = ImAio.getAllOnlineUser();
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10005);
		}else if(2 == userReqBody.getType()){
			clients = ImAio.getAllUser();
			resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10006);
		}
		if(clients == null)
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10004), channelContext);
		resPacket.setData(clients);
		return ImKit.ConvertRespPacket(resPacket, channelContext);
	}

}
