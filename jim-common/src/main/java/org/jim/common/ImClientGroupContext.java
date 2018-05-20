/**
 * 
 */
package org.jim.common;

import java.util.concurrent.ThreadPoolExecutor;

import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * @author WChao
 *
 */
public class ImClientGroupContext extends ClientGroupContext{

	public ImClientGroupContext(ClientAioHandler aioHandler,ClientAioListener aioListener) {
		super(aioHandler, aioListener);
	}

	public ImClientGroupContext(ClientAioHandler aioHandler,ClientAioListener aioListener, ReconnConf reconnConf,SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		super(aioHandler, aioListener, reconnConf, tioExecutor, groupExecutor);
	}

	public ImClientGroupContext(ClientAioHandler aioHandler,ClientAioListener aioListener, ReconnConf reconnConf) {
		super(aioHandler, aioListener, reconnConf);
	}

}
