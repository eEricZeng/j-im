/**
 * 
 */
package org.tio.im.server.command.handler.processor.handshake;

import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.server.command.handler.processor.ProcessorIntf;
/**
 * @author WChao
 *
 */
public interface HandshakeProcessorIntf extends ProcessorIntf{
	
	public ImPacket handshake(ImPacket packet,ChannelContext channelContext)  throws Exception;
}
