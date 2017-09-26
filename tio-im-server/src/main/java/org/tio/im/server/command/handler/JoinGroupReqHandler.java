package org.tio.im.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.Group;
import org.tio.im.common.packets.JoinGroupNotifyRespBody;
import org.tio.im.common.packets.JoinGroupRespBody;
import org.tio.im.common.packets.JoinGroupResult;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.CmdHandler;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.util.BeanUtil;

/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月21日 下午3:33:23
 */
public class JoinGroupReqHandler extends CmdHandler {
	
	private static Logger log = LoggerFactory.getLogger(JoinGroupReqHandler.class);
	
	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}

		Group joinGroup = JSONObject.parseObject(packet.getBody(),Group.class);

		String groupId = joinGroup.getId();
		if (StringUtils.isBlank(groupId)) {
			log.error("group is null,{}", channelContext);
			Aio.close(channelContext, "group is null when join group");
			return null;
		}
		
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		Aio.bindGroup(channelContext, groupId);

		//回一条消息，告诉对方进群结果
		JoinGroupResult joinGroupResult = JoinGroupResult.JOIN_GROUP_RESULT_OK;
		
		JoinGroupRespBody joinGroupRespBody = new JoinGroupRespBody();
		joinGroupRespBody.setGroup(groupId);
		joinGroupRespBody.setResult(joinGroupResult);
		
		RespBody joinRespBody = new RespBody(Command.COMMAND_JOIN_GROUP_RESP,ImStatus.C400).setData(joinGroupRespBody.toString());
		ImPacket respPacket = Resps.convertRespPacket(joinRespBody, channelContext);
		
		User notifyUser = new User();
		BeanUtil.copyProperties(imSessionContext.getClient().getUser(), notifyUser);
		notifyUser.setGroups(null);
		
		//发进房间通知  COMMAND_JOIN_GROUP_NOTIFY_RESP
		JoinGroupNotifyRespBody joinGroupNotifyRespBody = new JoinGroupNotifyRespBody().setGroup(groupId).setUser(notifyUser);
		RespBody notifyRespBody = new RespBody(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP).setData(joinGroupNotifyRespBody.toString());
		
		ImPacket joinGroupNotifyrespPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP, JSONObject.toJSONBytes(notifyRespBody));
		ImAio.sendToGroup(channelContext.getGroupContext(), groupId, joinGroupNotifyrespPacket);
		
		return respPacket;
	}
	@Override
	public Command command() {
		// TODO Auto-generated method stub
		return Command.COMMAND_JOIN_GROUP_REQ;
	}
}
