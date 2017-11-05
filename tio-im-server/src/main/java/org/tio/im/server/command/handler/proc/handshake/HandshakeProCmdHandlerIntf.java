/**
 * 
 */
package org.tio.im.server.command.handler.proc.handshake;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;

/**
 * @author WChao
 *
 */
public interface HandshakeProCmdHandlerIntf extends ProCmdHandlerIntf{
	
	public ImPacket handshake(ImPacket packet,ChannelContext channelContext)  throws Exception;
}
