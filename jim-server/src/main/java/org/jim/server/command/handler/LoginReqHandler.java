package org.jim.server.command.handler;

import org.jim.common.ImAio;
import org.jim.common.ImConfig;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.ImStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbCmdHandler;
import org.jim.server.command.handler.processor.ProcessorIntf;
import org.jim.server.command.handler.processor.login.LoginProcessorIntf;

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
			Aio.remove(channelContext, "loginname and token is null");
			return null;
		}
		String userid = user.getId();
		LoginRespBody loginRespBodyBuilder = new LoginRespBody();
		String token = imSessionContext.getToken();
		user.setTerminal(ImKit.getTerminal(channelContext));
		imSessionContext.getClient().setUser(user);
		ImAio.bindUser(channelContext,userid,ImConfig.getMessageHelper().getBindListener());
		loginRespBodyBuilder.setUser(user);
		loginRespBodyBuilder.setToken(token);
		RespBody respBody = new RespBody(Command.COMMAND_LOGIN_RESP,ImStatus.C10007).setData(loginRespBodyBuilder);
		return ImKit.ConvertRespPacket(respBody, channelContext);
	}
	
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
