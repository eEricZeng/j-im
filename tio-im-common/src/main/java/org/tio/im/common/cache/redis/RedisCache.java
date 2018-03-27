package org.tio.im.common.cache.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.cache.redis.JedisTemplate.Pair;
import org.tio.im.common.cache.redis.JedisTemplate.PairEx;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.ICache;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author wchao
 * 2017年8月10日 下午1:35:01
 */
public class RedisCache implements ICache {
	
	private Logger log = LoggerFactory.getLogger(RedisCache.class);
	
	public static String cacheKey(String cacheName, String key) {
		return keyPrefix(cacheName) + key;
	}

	public static String keyPrefix(String cacheName) {
		return cacheName + ":";
	}

	public static void main(String[] args) {
	}

	private String cacheName = null;

	private Long timeToLiveSeconds = null;

	private Long timeToIdleSeconds = null;

	private Long timeout = null;

	public RedisCache(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		this.cacheName = cacheName;
		this.timeToLiveSeconds = timeToLiveSeconds;
		this.timeToIdleSeconds = timeToIdleSeconds;
		this.timeout = this.timeToLiveSeconds == null ? this.timeToIdleSeconds : this.timeToLiveSeconds;

	}

	@Override
	public void clear() {
		long start = SystemTimer.currentTimeMillis();
		try {
			JedisTemplate.me().delKeysLike(keyPrefix(cacheName));
		} catch (Exception e) {
			log.error(e.toString(),e);
		}
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		log.info("clear cache {}, cost {}ms", cacheName, iv);
	}

	@Override
	public Serializable get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		Serializable value = null;
		try {
			value = JedisTemplate.me().get(cacheKey(cacheName, key), Serializable.class);
			if (timeToIdleSeconds != null) {
				if (value != null) {
					RedisExpireUpdateTask.add(cacheName, key, value ,timeout);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(),e);
		}
		return value;
	}

	@Override
	public Collection<String> keys() {
		try {
			return JedisTemplate.me().keys(keyPrefix(cacheName));
		} catch (Exception e) {
			log.error(e.toString(),e);
		}
		return null;
	}

	@Override
	public void put(String key, Serializable value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		try {
			JedisTemplate.me().set(cacheKey(cacheName, key), value, Integer.parseInt(timeout+""));
		}catch (Exception e) {
			log.error(e.toString(),e);
		}
	}
	public void putAll(List<Pair<String,Serializable>> values) {
		if (values == null || values.size() < 1) {
			return;
		}
		int expire = Integer.parseInt(timeout+"");
		try {
			List<PairEx<String,String,Integer>> pairDatas = new ArrayList<PairEx<String,String,Integer>>();
			for(Pair<String,Serializable> pair : values){
				pairDatas.add(JedisTemplate.me().makePairEx(cacheKey(cacheName, pair.getKey()),JSONObject.toJSONString(pair.getValue()),expire));
			}
			JedisTemplate.me().batchSetStringEx(pairDatas);
		}catch (Exception e) {
			log.error(e.toString(),e);
		}
	}
	@Override
	public void putTemporary(String key, Serializable value) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		try {
			JedisTemplate.me().set(cacheKey(cacheName, key), value,10);
		} catch (Exception e) {
			log.error(e.toString(),e);
		}
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		try {
			JedisTemplate.me().delKey(cacheKey(cacheName, key));
		} catch (Exception e) {
			log.error(e.toString(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		return (T)get(cacheKey(cacheName, key));
	}

	public String getCacheName() {
		return cacheName;
	}

	public Long getTimeout() {
		return timeout;
	}

	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}
}
