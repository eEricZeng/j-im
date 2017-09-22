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
import org.tio.im.common.packets.User;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.CmdHandler;

import com.alibaba.fastjson.JSONObject;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月18日 下午4:08:47
 */
public class GetUserReqHandler extends CmdHandler{

	@Override
	public Command command() {
		return Command.COMMAND_GET_USER_REQ;
	}

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		User user = JSONObject.parseObject(packet.getBody(),User.class);
		Object clients = null;
		if(user.getType() == null || "0".equals(user.getType())){
			clients = ImAio.getUser(channelContext.getGroupContext(), user.getId());
		}else if( "1".equals(user.getType())){
			clients = ImAio.getAllOnlineUser(channelContext.getGroupContext());
		}else if("2".equals(user.getType())){
			clients = ImAio.getAllUser(channelContext.getGroupContext());
		}
		if(clients == null)
			return null;
		RespBody resPacket = new RespBody().setCode(ImStatus.C100.getCode()).setCommand(Command.COMMAND_GET_USER_RESP).setMsg(JSONObject.toJSONString(clients));
		return Resps.convertRespPacket(resPacket, channelContext);
	}

}
