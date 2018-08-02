/**
 * 
 */
package org.jim.server.command.handler.processor.login;

import org.tio.core.ChannelContext;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 *
 */
public interface LoginProcessorIntf extends ProcessorIntf{
	
	public LoginRespBody doLogin(LoginReqBody loginReqBody ,ChannelContext channelContext);

	public void onSuccess(ChannelContext channelContext);
}
