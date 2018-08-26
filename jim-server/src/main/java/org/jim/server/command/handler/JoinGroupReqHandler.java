package org.jim.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.ImStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.JoinGroupNotifyRespBody;
import org.jim.common.packets.JoinGroupRespBody;
import org.jim.common.packets.JoinGroupResult;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbCmdHandler;
/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月21日 下午3:33:23
 */
public class JoinGroupReqHandler extends AbCmdHandler {
	
	private static Logger log = LoggerFactory.getLogger(JoinGroupReqHandler.class);
	
	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		
		ImPacket joinGroupRespPacket = bindGroup(packet, channelContext);//绑定群组;
		joinGroupNotify(packet,channelContext);//发送进房间通知;

		return joinGroupRespPacket;
	}
	/**
	 * 发送进房间通知;
	 * @param packet
	 * @param channelContext
	 */
	public void joinGroupNotify(ImPacket packet,ChannelContext channelContext){
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		
		User clientUser = imSessionContext.getClient().getUser();
		User notifyUser = new User(clientUser.getId(),clientUser.getNick());
		
		Group joinGroup = JsonKit.toBean(packet.getBody(),Group.class);
		String groupId = joinGroup.getGroup_id();
		//发进房间通知  COMMAND_JOIN_GROUP_NOTIFY_RESP
		JoinGroupNotifyRespBody joinGroupNotifyRespBody = new JoinGroupNotifyRespBody().setGroup(groupId).setUser(notifyUser);
		RespBody notifyRespBody = new RespBody(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP,joinGroupNotifyRespBody);
		
		ImPacket joinGroupNotifyrespPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP,notifyRespBody.toByte());
		ImAio.sendToGroup(groupId, joinGroupNotifyrespPacket);
	}
	/**
	 * 绑定群组
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 */
	public ImPacket bindGroup(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}
		Group joinGroup = JsonKit.toBean(packet.getBody(),Group.class);

		String groupId = joinGroup.getGroup_id();
		if (StringUtils.isBlank(groupId)) {
			log.error("group is null,{}", channelContext);
			Aio.close(channelContext, "group is null when join group");
			return null;
		}
		
		ImAio.bindGroup(channelContext, groupId,imConfig.getMessageHelper().getBindListener());

		//回一条消息，告诉对方进群结果
		JoinGroupResult joinGroupResult = JoinGroupResult.JOIN_GROUP_RESULT_OK;
		
		JoinGroupRespBody joinGroupRespBody = new JoinGroupRespBody();
		joinGroupRespBody.setGroup(groupId);
		joinGroupRespBody.setResult(joinGroupResult);
		
		RespBody joinRespBody = new RespBody(Command.COMMAND_JOIN_GROUP_RESP,ImStatus.C10011).setData(joinGroupRespBody);
		ImPacket respPacket = ImKit.ConvertRespPacket(joinRespBody, channelContext);
		return respPacket;
	}
	@Override
	public Command command() {
		
		return Command.COMMAND_JOIN_GROUP_REQ;
	}
}
