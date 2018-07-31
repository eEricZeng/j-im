package org.jim.server.command.handler;

import java.util.List;

import org.jim.common.Const;
import org.jim.common.ImAio;
import org.jim.common.ImConfig;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.ImStatus;
import org.jim.common.message.IMesssageHelper;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.protocol.IProtocol;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbCmdHandler;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.processor.ProcessorIntf;
import org.jim.server.command.handler.processor.login.LoginProcessorIntf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;

public class LoginReqHandler extends AbCmdHandler {
	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Aio.remove(channelContext, "body is null");
			return null;
		}
		ProcessorIntf loginProcessor = this.getProcessor(channelContext);
		if(loginProcessor == null){
			log.info("登录失败,没有业务处理器!");
			Aio.remove(channelContext, "no login serviceHandler processor!");
			return null;
		}
		LoginProcessorIntf loginServiceHandler = (LoginProcessorIntf)loginProcessor;
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		LoginReqBody loginReqBody = JsonKit.toBean(packet.getBody(),LoginReqBody.class);
		
		User user = loginServiceHandler.getUser(loginReqBody,channelContext);
		if (user == null ) {
			log.info("登录失败, loginname:{}, password:{}", loginReqBody.getLoginname(), loginReqBody.getPassword());
			loginServiceHandler.onFailed(channelContext);
			Aio.remove(channelContext, "loginname and token is null");
			return null;
		}
		String userid = user.getId();
		LoginRespBody loginRespBodyBuilder = new LoginRespBody();
		String token = imSessionContext.getToken();
		IProtocol protocol = ImKit.protocol(null, channelContext);
		String terminal = protocol == null ? "" : protocol.name();
		user.setTerminal(terminal);
		imSessionContext.getClient().setUser(user);
		ImAio.bindUser(channelContext,userid,ImConfig.getMessageHelper().getBindListener());
		bindUnbindGroup(channelContext, user);//初始化绑定或者解绑群组;
		loginRespBodyBuilder.setUser(user);
		loginRespBodyBuilder.setToken(token);
		loginServiceHandler.onSuccess(channelContext);
		RespBody respBody = new RespBody(Command.COMMAND_LOGIN_RESP,ImStatus.C10007).setData(loginRespBodyBuilder);
		return ImKit.ConvertRespPacket(respBody, channelContext);
	}
	/**
	 * 初始化绑定或者解绑群组;
	 */
	public void bindUnbindGroup(ChannelContext channelContext , User user)throws Exception{
		String userid = user.getId();
		List<Group> groups = user.getGroups();
		if( groups != null){
			boolean isStore = Const.ON.equals(ImConfig.isStore);
			IMesssageHelper messageHelper = null;
			List<String> groupIds = null;
			if(isStore){
				messageHelper = ImConfig.getMessageHelper();
				groupIds = messageHelper.getGroups(userid);
			}
			for(Group group : groups){//绑定群组
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
				for(String groupid : groupIds){
					messageHelper.getBindListener().onAfterGroupUnbind(channelContext, groupid);
				}
			}
		}
	}
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
