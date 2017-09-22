package org.tio.im.server.command.handler;

import org.apache.commons.lang3.StringUtils;
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
import org.tio.im.server.command.CmdHandler;
import org.tio.im.server.service.UserService;

import com.alibaba.fastjson.JSONObject;

public class LoginReqHandler extends CmdHandler {
	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Aio.remove(channelContext, "body is null");
			return null;
		}
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		String handshakeToken = imSessionContext.getToken();
		LoginReqBody loginReqBody = JSONObject.parseObject(packet.getBody(),LoginReqBody.class);
		String token = loginReqBody.getToken();
		String loginname = loginReqBody.getLoginname();
		String password = loginReqBody.getPassword();

		User user = null;
		if (!StringUtils.isBlank(handshakeToken)) {
			user = UserService.getUser(handshakeToken);
		}
		if (user == null) {
			if (!StringUtils.isBlank(loginname)) {
				user = UserService.getUser(loginname, password);
			} else if (!StringUtils.isBlank(token)) {
				user = UserService.getUser(token);
			}
		}
		if (user == null) {
			log.info("登录失败, loginname:{}, password:{}", loginname, password);
			Aio.remove(channelContext, "loginname and token is null");
			return null;
		}
		String userid = user.getId();
		LoginRespBody loginRespBodyBuilder = new LoginRespBody();
		Aio.bindUser(channelContext,userid);
		if (StringUtils.isBlank(token)) {
			token = UserService.newToken();
		}
		imSessionContext.setToken(token);
		if(imSessionContext.getClient() == null){
			ImUtils.setClient(channelContext);
		}
		imSessionContext.getClient().setUser(user);
		loginRespBodyBuilder.setUser(user);
		loginRespBodyBuilder.setToken(token);
		RespBody respBody = new RespBody(Command.COMMAND_LOGIN_RESP).setCode(ImStatus.C200.getCode()).setMsg(JSONObject.toJSONString(loginRespBodyBuilder));
		return Resps.convertRespPacket(respBody, channelContext);
	}
	
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
