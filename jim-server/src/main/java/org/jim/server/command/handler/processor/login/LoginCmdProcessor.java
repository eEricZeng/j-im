/**
 * 
 */
package org.jim.server.command.handler.processor.login;

import org.tio.core.ChannelContext;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.server.command.handler.processor.CmdProcessor;
/**
 *
 * @author WChao
 */
public interface LoginCmdProcessor extends CmdProcessor {
	/**
	 * 执行登录操作接口方法
	 * @param loginReqBody
	 * @param channelContext
	 * @return
	 */
	public LoginRespBody doLogin(LoginReqBody loginReqBody ,ChannelContext channelContext);

	/**
	 * 登录成功回调方法
	 * @param channelContext
	 */
	public void onSuccess(ChannelContext channelContext);
}
