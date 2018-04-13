/**
 * 
 */
package org.jim.server.command.handler.processor.handshake;

import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;
import org.jim.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 *
 */
public interface HandshakeProcessorIntf extends ProcessorIntf{
	
	public ImPacket handshake(ImPacket packet,ChannelContext channelContext)  throws Exception;
}
