package org.tio.im.common.cache.caffeine;

import com.jfinal.kit.Prop;

/**
 * @author WChao
 * @date 2018年3月9日 上午1:09:03
 */
public class CaffeineConfiguration {
	
	private String cacheName;
	private Long  timeToLiveSeconds = 1800L;
	private Long  timeToIdleSeconds = 1800L;
	private Integer  maximumSize = 5000000;
	private Integer initialCapacity = 10;
	private boolean recordStats = false;
	
	public CaffeineConfiguration(){}
	
	public CaffeineConfiguration(String cacheName,Prop prop){
		this.cacheName = cacheName;
		String[] values = prop.get(cacheName,"5000000,1800").split(",");
		this.maximumSize = Integer.valueOf(values[0]);
		if(values.length>1){
			this.timeToLiveSeconds = Long.valueOf(values[1]);
			this.timeToIdleSeconds = Long.valueOf(values[1]);
		}
	}
	public String getCacheName() {
		return cacheName;
	}
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}
	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}
	public void setTimeToLiveSeconds(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}
	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}
	public void setTimeToIdleSeconds(Long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}
	public Integer getMaximumSize() {
		return maximumSize;
	}
	public void setMaximumSize(Integer maximumSize) {
		this.maximumSize = maximumSize;
	}
	public Integer getInitialCapacity() {
		return initialCapacity;
	}
	public void setInitialCapacity(Integer initialCapacity) {
		this.initialCapacity = initialCapacity;
	}
	public boolean isRecordStats() {
		return recordStats;
	}
	public void setRecordStats(boolean recordStats) {
		this.recordStats = recordStats;
	}
}
