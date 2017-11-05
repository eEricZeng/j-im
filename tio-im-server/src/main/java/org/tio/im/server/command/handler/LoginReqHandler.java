package org.tio.im.server.command.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.LoginReqBody;
import org.tio.im.common.packets.LoginRespBody;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.AbCmdHandler;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;
import org.tio.im.server.command.handler.proc.login.LoginCmdHandlerIntf;
import com.alibaba.fastjson.JSONObject;

public class LoginReqHandler extends AbCmdHandler {
	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Aio.remove(channelContext, "body is null");
			return null;
		}
		ProCmdHandlerIntf loginProCmdHandler = this.getProcCmdHandler(channelContext);
		if(loginProCmdHandler == null){
			log.info("登录失败,没有业务处理器!");
			Aio.remove(channelContext, "no login serviceHandler processor!");
			return null;
		}
		LoginCmdHandlerIntf loginServiceHandler = (LoginCmdHandlerIntf)loginProCmdHandler;
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		LoginReqBody loginReqBody = JSONObject.parseObject(packet.getBody(),LoginReqBody.class);
		
		User user = loginServiceHandler.getUser(loginReqBody,channelContext);
		if (user == null ) {
			log.info("登录失败, loginname:{}, password:{}", loginReqBody.getLoginname(), loginReqBody.getPassword());
			Aio.remove(channelContext, "loginname and token is null");
			return null;
		}
		String userid = user.getId();
		LoginRespBody loginRespBodyBuilder = new LoginRespBody();
		Aio.bindUser(channelContext,userid);
		if(imSessionContext.getClient() == null){
			ImUtils.setClient(channelContext);
		}
		String token = imSessionContext.getToken();
		imSessionContext.getClient().setUser(user);
		loginRespBodyBuilder.setUser(user);
		loginRespBodyBuilder.setToken(token);
		RespBody respBody = new RespBody(Command.COMMAND_LOGIN_RESP,ImStatus.C200).setData(loginRespBodyBuilder.toString());
		return Resps.convertRespPacket(respBody, channelContext);
	}
	
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
