package org.tio.im.common.cache.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时更新redis的过期时间
 * @author wchao
 * 2017年8月14日 下午1:34:06
 */
public class RedisExpireUpdateTask {
	private static Logger log = LoggerFactory.getLogger(RedisExpireUpdateTask.class);

	private static boolean started = false;

	private static LinkedBlockingQueue<ExpireVo> redisExpireVoQueue = new LinkedBlockingQueue<ExpireVo>();

	public static void add(String cacheName, String key, Serializable value, long expire) {
		ExpireVo expireVo = new ExpireVo(cacheName, key, value, expire);
		redisExpireVoQueue.offer(expireVo);
	}

	public static void start() {
		if (started) {
			return;
		}
		synchronized (RedisExpireUpdateTask.class) {
			if (started) {
				return;
			}
			started = true;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				List<ExpireVo> l2Datas = new ArrayList<ExpireVo>();
				while (true) {
					try {
						if(l2Datas.size() == 2000 || redisExpireVoQueue.isEmpty()){//2000一提交(防止频繁访问Redis网络I/O消耗压力)
							for (ExpireVo expireVo : l2Datas) {
								log.debug("更新缓存过期时间, cacheName:{}, key:{}, expire:{}", expireVo.getCacheName(), expireVo.getKey(), expireVo.getExpire());
								Serializable value = expireVo.getValue();
								if(value != null)
								JedisTemplate.me().set(expireVo.getCacheName()+":"+expireVo.getKey(), value, Integer.parseInt(expireVo.getExpire()+""));
							}
							l2Datas.clear();
							try {
								Thread.sleep(1000 * 10);
							} catch (InterruptedException e) {
								log.error(e.toString(), e);
							}
						}
						else{
							ExpireVo expireVo = redisExpireVoQueue.poll();
							if(expireVo != null){
								l2Datas.add(expireVo);
							}
						}
					} catch (Throwable e) {
						log.error(e.getMessage(), e);
					}
				}

			}
		}, RedisExpireUpdateTask.class.getName()).start();
	}

	/**
	 *
	 * @author wchao
	 */
	private RedisExpireUpdateTask() {
		
	}
}
