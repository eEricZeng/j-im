package org.jim.server.command.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.jim.common.ImConst;
import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.ImStatus;
import org.jim.common.message.MessageHelper;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.common.packets.User;
import org.jim.common.protocol.IProtocol;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.processor.login.LoginCmdProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * 登录消息命令处理器
 * @author WChao
 * @date 2018年4月10日 下午2:40:07
 */
public class LoginReqHandler extends AbstractCmdHandler {
	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Aio.remove(channelContext, "body is null");
			return null;
		}
		List<LoginCmdProcessor> loginProcessors = this.getProcessor(channelContext, LoginCmdProcessor.class);
		if(CollectionUtils.isEmpty(loginProcessors)){
			log.info("登录失败,没有登录命令业务处理器!");
			Aio.remove(channelContext, "no login serviceHandler processor!");
			return null;
		}
		LoginCmdProcessor loginServiceHandler = loginProcessors.get(0);
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		LoginReqBody loginReqBody = JsonKit.toBean(packet.getBody(),LoginReqBody.class);
		
		LoginRespBody loginRespBody = loginServiceHandler.doLogin(loginReqBody,channelContext);
		if (loginRespBody == null || loginRespBody.getUser() == null) {
			log.info("登录失败, loginName:{}, password:{}", loginReqBody.getLoginname(), loginReqBody.getPassword());
			if(loginRespBody == null){
				loginRespBody = new LoginRespBody(Command.COMMAND_LOGIN_RESP, ImStatus.C10008);
			}
			loginRespBody.clear();
			ImPacket loginRespPacket = new ImPacket(Command.COMMAND_LOGIN_RESP, loginRespBody.toByte());
			ImAio.bSend(channelContext,loginRespPacket);
			ImAio.remove(channelContext, "loginName and token is incorrect");
			return null;
		}
		User user = loginRespBody.getUser();
		String userId = user.getId();
		IProtocol protocol = ImKit.protocol(null, channelContext);
		String terminal = protocol == null ? "" : protocol.name();
		user.setTerminal(terminal);
		imSessionContext.getClient().setUser(user);
		ImAio.bindUser(channelContext,userId,imConfig.getMessageHelper().getBindListener());
		//初始化绑定或者解绑群组;
		bindUnbindGroup(channelContext, user);
		loginServiceHandler.onSuccess(channelContext);
		loginRespBody.clear();
		ImPacket loginRespPacket = new ImPacket(Command.COMMAND_LOGIN_RESP, loginRespBody.toByte());
		return loginRespPacket;
	}
	/**
	 * 初始化绑定或者解绑群组;
	 */
	public void bindUnbindGroup(ChannelContext channelContext , User user)throws Exception{
		String userId = user.getId();
		List<Group> groups = user.getGroups();
		if( groups != null){
			boolean isStore = ImConst.ON.equals(imConfig.getIsStore());
			MessageHelper messageHelper = null;
			List<String> groupIds = null;
			if(isStore){
				messageHelper = imConfig.getMessageHelper();
				groupIds = messageHelper.getGroups(userId);
			}
			//绑定群组
			for(Group group : groups){
				if(isStore && groupIds != null){
					groupIds.remove(group.getGroup_id());
				}
				ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ,JsonKit.toJsonBytes(group));
				try {
					JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
					joinGroupReqHandler.bindGroup(groupPacket, channelContext);
				} catch (Exception e) {
					log.error(e.toString(),e);
				}
			}
			if(isStore && groupIds != null){
				for(String groupId : groupIds){
					messageHelper.getBindListener().onAfterGroupUnbind(channelContext, groupId);
				}
			}
		}
	}
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
