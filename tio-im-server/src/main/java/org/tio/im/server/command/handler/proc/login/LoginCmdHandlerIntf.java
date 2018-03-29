/**
 * 
 */
package org.tio.im.server.command.handler.proc.login;

import org.tio.core.ChannelContext;
import org.tio.im.common.packets.LoginReqBody;
import org.tio.im.common.packets.User;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;

/**
 * @author WChao
 *
 */
public interface LoginCmdHandlerIntf extends ProCmdHandlerIntf{
	
	public User getUser(LoginReqBody loginReqBody ,ChannelContext channelContext);
}
