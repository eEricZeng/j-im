package org.tio.im.common.cache.caffeineredis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.cache.caffeine.CaffeineCache;
import org.tio.im.common.cache.caffeine.CaffeineCacheManager;
import org.tio.im.common.cache.caffeine.CaffeineConfiguration;
import org.tio.im.common.cache.caffeine.CaffeineConfigurationFactory;
import org.tio.im.common.cache.redis.RedisCache;
import org.tio.im.common.cache.redis.RedisCacheManager;
import org.tio.im.common.cache.redis.SubRunnable;

/**
 * @author WChao
 * @date 2018年3月8日 下午2:28:14
 */
public class CaffeineRedisCacheManager {
	
	private static Logger log = LoggerFactory.getLogger(CaffeineRedisCacheManager.class);
	
	private static Map<String, CaffeineRedisCache> map = new HashMap<>();
	
	private static boolean inited = false;
	
	public static final String CACHE_CHANGE_TOPIC = "REDIS_CACHE_CHANGE_TOPIC_CAFFEINE";
	//L2异步存储队列;
	private static RedisAsyncRunnable asyncRedisQueue =  new RedisAsyncRunnable();
	/**
	 * 在本地最大的过期时间，这样可以防止内存爆掉，单位：秒
	 */
	public static int MAX_EXPIRE_IN_LOCAL = 1800;
	
	private CaffeineRedisCacheManager(){}

	static{
		try{
			List<CaffeineConfiguration> configurations = CaffeineConfigurationFactory.parseConfiguration();
			for(CaffeineConfiguration configuration : configurations){
				 register(configuration.getCacheName(), configuration.getTimeToLiveSeconds(),configuration.getTimeToIdleSeconds());
			}
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	public static CaffeineRedisCache getCache(String cacheName) {
		CaffeineRedisCache caffeineRedisCache = map.get(cacheName);
		if (caffeineRedisCache == null) {
			log.warn("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, CaffeineRedisCache.class.getSimpleName());
		}
		return caffeineRedisCache;
	}
	
	private static void init() {
		if (!inited) {
			synchronized (CaffeineRedisCacheManager.class) {
				if (!inited) {
					new Thread(new SubRunnable(CACHE_CHANGE_TOPIC)).start();
					new Thread(asyncRedisQueue).start();
					inited = true;
				}
			}
		}
	}
	
	public static CaffeineRedisCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		init();
		CaffeineRedisCache caffeineRedisCache = map.get(cacheName);
		if (caffeineRedisCache == null) {
			synchronized (CaffeineRedisCacheManager.class) {
				caffeineRedisCache = map.get(cacheName);
				if (caffeineRedisCache == null) {
					RedisCache redisCache = RedisCacheManager.register(cacheName, timeToLiveSeconds, timeToIdleSeconds);
					
					Long timeToLiveSecondsForCaffeine = timeToLiveSeconds;
					Long timeToIdleSecondsForCaffeine = timeToIdleSeconds;
					
					if (timeToLiveSecondsForCaffeine != null) {
						timeToLiveSecondsForCaffeine = Math.min(timeToLiveSecondsForCaffeine, MAX_EXPIRE_IN_LOCAL);
					}
					if (timeToIdleSecondsForCaffeine != null) {
						timeToIdleSecondsForCaffeine = Math.min(timeToIdleSecondsForCaffeine, MAX_EXPIRE_IN_LOCAL);
					}
					CaffeineCache caffeineCache = CaffeineCacheManager.register(cacheName, timeToLiveSecondsForCaffeine, timeToIdleSecondsForCaffeine);

					caffeineRedisCache = new CaffeineRedisCache(cacheName, caffeineCache, redisCache);
					map.put(cacheName, caffeineRedisCache);
				}
			}
		}
		return caffeineRedisCache;
	}

	public static RedisAsyncRunnable getAsyncRedisQueue() {
		return asyncRedisQueue;
	}
}
