package org.jim.common.cluster;

import java.util.UUID;

import org.jim.common.ImPacket;
/**
 * 成员变量group, userid, ip谁有值就发给谁，toAll为true则发给所有<br>
 * packet是不允许为null的
 * @author WChao 
 * 2018年05月20日 下午3:10:29
 */
public class ImClusterVo implements java.io.Serializable {
	private static final long serialVersionUID = 6978027913776155664L;
	
	public static final String CLIENTID = UUID.randomUUID().toString();

	private ImPacket packet;

	private String clientId = CLIENTID;
	
	private String group;

	private String userid;
	
	private String token;
	
	private String ip;
	
	/**
	 * ChannelContext'id
	 */
	private String channelId;
	
	private boolean toAll = false;

	public ImPacket getPacket() {
		return packet;
	}

	public void setPacket(ImPacket packet) {
		this.packet = packet;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 
	 * @author: WChao
	 */
	public ImClusterVo() {
	}
	
	public ImClusterVo(ImPacket packet) {
		this.packet = packet;
	}

	/**
	 * @param args
	 * @author: WChao
	 */
	public static void main(String[] args) {

	}

	public boolean isToAll() {
		return toAll;
	}

	public void setToAll(boolean toAll) {
		this.toAll = toAll;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
