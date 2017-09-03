/**
 * 
 */
package org.tio.im.server;

import org.tio.im.server.handler.ImServerAioHandler;
import org.tio.im.server.listener.ImServerAioListener;
import org.tio.server.ServerGroupContext;

/**
 * @author WChao
 *
 */
public class ImServerGroupContext extends ServerGroupContext {

	public ImServerGroupContext(ImServerAioHandler imServerAioHandler,ImServerAioListener imServerAioListener) {
		super(imServerAioHandler, imServerAioListener);
		imServerAioHandler.init(this);
	}

}
