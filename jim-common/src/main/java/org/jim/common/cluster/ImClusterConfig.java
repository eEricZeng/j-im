package org.jim.common.cluster;

import org.tio.core.GroupContext;

/**
 * 
 * @author WChao
 * 2018年05月20日 下午1:09:16
 */
public abstract class ImClusterConfig {
	
	/**
	 * 群组是否集群（同一个群组是否会分布在不同的机器上），false:不集群，默认不集群
	 */
	private boolean cluster4group = true;
	/**
	 * 用户是否集群（同一个用户是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean cluster4user = true;
	/**
	 * ip是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean cluster4ip = true;
	/**
	 * id是否集群（在A机器上的客户端是否可以通过channelId发消息给B机器上的客户端），false:不集群，默认集群<br>
	 */
	private boolean cluster4channelId = true;
	/**
	 * 所有连接是否集群（同一个ip是否会分布在不同的机器上），false:不集群，默认集群
	 */
	private boolean cluster4all = true;

	protected GroupContext groupContext = null;
	
	public abstract void send(ImClusterVo imClusterVo);
	public abstract void sendAsyn(ImClusterVo imClusterVo);
	
	public boolean isCluster4group() {
		return cluster4group;
	}

	public void setCluster4group(boolean cluster4group) {
		this.cluster4group = cluster4group;
	}

	public boolean isCluster4user() {
		return cluster4user;
	}

	public void setCluster4user(boolean cluster4user) {
		this.cluster4user = cluster4user;
	}

	public boolean isCluster4ip() {
		return cluster4ip;
	}

	public void setCluster4ip(boolean cluster4ip) {
		this.cluster4ip = cluster4ip;
	}

	public boolean isCluster4all() {
		return cluster4all;
	}

	public void setCluster4all(boolean cluster4all) {
		this.cluster4all = cluster4all;
	}

	public boolean isCluster4channelId() {
		return cluster4channelId;
	}

	public void setCluster4channelId(boolean cluster4channelId) {
		this.cluster4channelId = cluster4channelId;
	}
}
