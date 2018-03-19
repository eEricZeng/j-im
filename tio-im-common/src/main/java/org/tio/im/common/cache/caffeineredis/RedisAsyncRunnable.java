package org.tio.im.common.cache.caffeineredis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.cache.CacheChangeType;
import org.tio.im.common.cache.CacheChangedVo;
import org.tio.im.common.cache.redis.JedisTemplate;
/**
 * @author WChao
 * @date 2018年3月13日 下午7:59:20
 */
public class RedisAsyncRunnable implements Runnable{
	
	private LinkedBlockingQueue<RedisL2Vo> redisL2VoQueue = new LinkedBlockingQueue<RedisL2Vo>();
	private static boolean started = false;
	private Logger LOG = LoggerFactory.getLogger(RedisAsyncRunnable.class);
	
	public void add(RedisL2Vo redisL2Vo){
		this.redisL2VoQueue.offer(redisL2Vo);
	}
	@Override
	public void run() {
		if (started) {
			return;
		}
		synchronized (RedisAsyncRunnable.class) {
			if (started) {
				return;
			}
			started = true;
		}
		List<RedisL2Vo> l2Datas = new ArrayList<RedisL2Vo>();
		while(true){
			try {
				if(l2Datas.size() == 2000 || redisL2VoQueue.isEmpty()){//2000一提交(防止频繁访问Redis网络I/O消耗压力)
					for(RedisL2Vo redisL2Vo : l2Datas){
						redisL2Vo.getRedisCache().put(redisL2Vo.getKey(),redisL2Vo.getValue());
						CacheChangedVo cacheChangedVo = new CacheChangedVo(redisL2Vo.getRedisCache().getCacheName(), redisL2Vo.getKey(), CacheChangeType.PUT);
						JedisTemplate.me().publish(CaffeineRedisCacheManager.CACHE_CHANGE_TOPIC,cacheChangedVo.toString());
					}
					l2Datas.clear();
					try {
						Thread.sleep(1000 * 10);
					} catch (InterruptedException e) {
						LOG.error(e.toString(), e);
					}
				}else{
					RedisL2Vo redisL2Vo = redisL2VoQueue.poll();
					if(redisL2Vo != null){
						l2Datas.add(redisL2Vo);
					}
				}
			} catch (Exception e) {
				LOG.error(e.toString(),e);
			}
		}
	}
}
