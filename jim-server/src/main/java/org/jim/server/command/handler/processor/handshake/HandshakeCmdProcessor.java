/**
 * 
 */
package org.jim.server.command.handler.processor.handshake;

import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;
import org.jim.server.command.handler.processor.CmdProcessor;
/**
 * @author WChao
 *
 */
public interface HandshakeCmdProcessor extends CmdProcessor {
	/**
	 * 对httpResponsePacket参数进行补充并返回，如果返回null表示不想和对方建立连接，框架会断开连接，如果返回非null，框架会把这个对象发送给对方
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: Wchao
	 */
	public ImPacket handshake(ImPacket packet,ChannelContext channelContext)  throws Exception;
	/**
	 * 握手成功后
	 * @param packet
	 * @param channelContext
	 * @throws Exception
	 * @author Wchao
	 */
	public void onAfterHandshaked(ImPacket packet, ChannelContext channelContext) throws Exception;
}
