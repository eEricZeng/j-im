/**
 * 
 */
package org.jim.server;

import java.util.concurrent.LinkedBlockingQueue;

import org.jim.common.Const;
import org.jim.common.ImConfig;
import org.jim.common.cache.redis.RedissonTemplate;
import org.jim.common.cluster.redis.RedisCluster;
import org.jim.common.cluster.redis.RedisClusterConfig;
import org.jim.server.handler.ImServerAioHandler;
import org.jim.server.handler.ProtocolHandlerManager;
import org.jim.server.listener.ImServerAioListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.server.ServerGroupContext;
import org.tio.utils.Threads;
import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * @author WChao
 *
 */
public class ImServerGroupContext extends ServerGroupContext{

	private Logger log = LoggerFactory.getLogger(ImServerGroupContext.class);
	
	private static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
	//IM配置加载类;
	private ImConfig imConfig;
	
	protected SynThreadPoolExecutor timExecutor = null;
	
	public ImServerGroupContext(ImConfig imConfig , ImServerAioHandler imServerAioHandler,ImServerAioListener imServerAioListener) {
		super(imServerAioHandler, imServerAioListener);
		this.imConfig = imConfig;
		this.setHeartbeatTimeout(imConfig.getHeartbeatTimeout());
		if(Const.ON.equals(ImConfig.isCluster)){//是否开启集群
			if(ImConfig.cluster == null){
				try{
					ImConfig.cluster = new RedisCluster(RedisClusterConfig.newInstance("REDIS_", RedissonTemplate.me().getRedissonClient(), this));
				}catch(Exception e){
					log.error("连接集群配置出现异常,请检查！",e);
				}
			}
		}else{
			ImConfig.cluster = null;
		}
		if (this.timExecutor == null) {
			LinkedBlockingQueue<Runnable> timQueue = new LinkedBlockingQueue<>();
			String timThreadName = Const.JIM;
			this.timExecutor = new SynThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, Threads.KEEP_ALIVE_TIME, timQueue,
					DefaultThreadFactory.getInstance(timThreadName, Thread.NORM_PRIORITY), timThreadName);
			this.timExecutor.prestartAllCoreThreads();
		}
		ImConfig.groupContext = this;
		ProtocolHandlerManager.init(imConfig);
	}

	public SynThreadPoolExecutor getTimExecutor() {
		return timExecutor;
	}

	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
	
}
