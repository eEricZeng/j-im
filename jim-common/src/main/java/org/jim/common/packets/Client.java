/**
 * 
 */
package org.jim.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 下午3:11:55
 */
public class Client extends Message{
	
	private static final long serialVersionUID = 6196600593975727155L;
	private String ip; //客户端ip
	private int port; //客户端port
	private User user; //如果没登录过，则为null
	private String region;  //地区
	private String useragent;  //浏览器信息
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getUseragent() {
		return useragent;
	}
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
}
