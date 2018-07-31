/**
 * 
 */
package org.jim.server.demo.listener;

import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.User;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.JoinGroupReqHandler;
import org.jim.server.listener.ImServerAioListener;
/**
 * @author WChao
 *
 */
public class ImDemoAioListener extends ImServerAioListener{
	
	private Logger log = LoggerFactory.getLogger(ImDemoAioListener.class);
	
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
	}
}
