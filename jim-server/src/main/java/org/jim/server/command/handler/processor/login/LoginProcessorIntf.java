/**
 * 
 */
package org.jim.server.command.handler.processor.login;

import org.tio.core.ChannelContext;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.User;
import org.jim.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 *
 */
public interface LoginProcessorIntf extends ProcessorIntf{
	
	public User getUser(LoginReqBody loginReqBody ,ChannelContext channelContext);

	public void onSuccess(ChannelContext channelContext);

	public void onFailed(ChannelContext channelContext);
}
