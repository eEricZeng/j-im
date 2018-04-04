/**
 * 
 */
package org.tio.im.server;

import java.util.concurrent.LinkedBlockingQueue;

import org.tio.im.common.ImConfig;
import org.tio.im.server.handler.ImServerAioHandler;
import org.tio.im.server.handler.ServerHandlerManager;
import org.tio.im.server.listener.ImServerAioListener;
import org.tio.server.ServerGroupContext;
import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * @author WChao
 *
 */
public class ImServerGroupContext extends ServerGroupContext {

	private static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
	
	protected SynThreadPoolExecutor timExecutor = null;
	
	public ImServerGroupContext(ImConfig imConfig , ImServerAioHandler imServerAioHandler,ImServerAioListener imServerAioListener) {
		super(imServerAioHandler, imServerAioListener);
		this.setHeartbeatTimeout(imConfig.getHeartbeatTimeout());
		if (this.timExecutor == null) {
			LinkedBlockingQueue<Runnable> timQueue = new LinkedBlockingQueue<>();
			String timThreadName = "tim";
			this.timExecutor = new SynThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, KEEP_ALIVE_TIME, timQueue,
					DefaultThreadFactory.getInstance(timThreadName, Thread.NORM_PRIORITY), timThreadName);
			this.timExecutor.prestartAllCoreThreads();
		}
		ImConfig.groupContext = this;
		ServerHandlerManager.init(imConfig);
	}

	public SynThreadPoolExecutor getTimExecutor() {
		return timExecutor;
	}
	
}
