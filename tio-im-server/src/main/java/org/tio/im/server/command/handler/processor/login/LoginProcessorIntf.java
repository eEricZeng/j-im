/**
 * 
 */
package org.tio.im.server.command.handler.processor.login;

import org.tio.core.ChannelContext;
import org.tio.im.common.packets.LoginReqBody;
import org.tio.im.common.packets.User;
import org.tio.im.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 *
 */
public interface LoginProcessorIntf extends ProcessorIntf{
	
	public User getUser(LoginReqBody loginReqBody ,ChannelContext channelContext);
}
