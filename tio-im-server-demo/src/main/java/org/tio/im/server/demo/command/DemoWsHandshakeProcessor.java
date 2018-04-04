/**
 * 
 */
package org.tio.im.server.demo.command;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.LoginReqBody;
import org.tio.im.common.ws.WsRequestPacket;
import org.tio.im.common.ws.WsResponsePacket;
import org.tio.im.common.ws.WsSessionContext;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.command.handler.LoginReqHandler;
import org.tio.im.server.command.handler.processor.handshake.WsHandshakeProcessor;

import com.alibaba.fastjson.JSONObject;

/**
 * @author WChao
 *
 */
public class DemoWsHandshakeProcessor extends WsHandshakeProcessor{
	
	/**
	 * WS握手方法，返回Null则业务层不同意握手，断开连接!
	 */
	@Override
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		WsRequestPacket wsRequestPacket = (WsRequestPacket) packet;
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
		if (wsRequestPacket.isHandShake()) {
			LoginReqHandler loginHandler = (LoginReqHandler)CommandManager.getCommand(Command.COMMAND_LOGIN_REQ);
			HttpRequest request = wsSessionContext.getHandshakeRequestPacket();
			String username = request.getParams().get("username") == null ? null : (String)request.getParams().get("username")[0];
			String password = request.getParams().get("password") == null ? null : (String)request.getParams().get("password")[0];
			String token = request.getParams().get("token") == null ? null : (String)request.getParams().get("token")[0];
			LoginReqBody loginBody = new LoginReqBody(username,password,token);
			byte[] loginBytes = JSONObject.toJSONBytes(loginBody);
			request.setBody(loginBytes);
			request.setBodyString(new String(loginBytes,HttpConst.CHARSET_NAME));
			Object loginResponse = loginHandler.handler(request, channelContext);
			if(loginResponse == null)
				return null;
			WsResponsePacket wsResponsePacket = new WsResponsePacket();
			wsResponsePacket.setHandShake(true);
			wsResponsePacket.setCommand(Command.COMMAND_HANDSHAKE_RESP);
			wsSessionContext.setHandshaked(true);
			return wsResponsePacket;
		}
		return null;
	}
}
