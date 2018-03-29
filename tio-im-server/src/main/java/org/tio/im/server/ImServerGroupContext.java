/**
 * 
 */
package org.tio.im.server;

import org.tio.im.common.ImConfig;
import org.tio.im.server.handler.ImServerAioHandler;
import org.tio.im.server.handler.ServerHandlerManager;
import org.tio.im.server.listener.ImServerAioListener;
import org.tio.server.ServerGroupContext;

/**
 * @author WChao
 *
 */
public class ImServerGroupContext extends ServerGroupContext {

	public ImServerGroupContext(ImConfig imConfig , ImServerAioHandler imServerAioHandler,ImServerAioListener imServerAioListener) {
		super(imServerAioHandler, imServerAioListener);
		this.setHeartbeatTimeout(imConfig.getHeartbeatTimeout());
		ImConfig.groupContext = this;
		ServerHandlerManager.init(imConfig);
	}

}
