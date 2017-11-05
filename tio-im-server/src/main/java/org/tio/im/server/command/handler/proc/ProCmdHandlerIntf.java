/**
 * 
 */
package org.tio.im.server.command.handler.proc;

import org.tio.core.ChannelContext;

/**
 * 不同协议CMD命令处理接口
 * @author WChao
 *
 */
public interface ProCmdHandlerIntf {
	
	public boolean isProtocol(ChannelContext channelContext);
	public String name();
	
}
