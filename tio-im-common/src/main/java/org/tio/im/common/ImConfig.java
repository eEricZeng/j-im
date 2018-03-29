/**
 * 
 */
package org.tio.im.common;

import org.tio.core.GroupContext;
import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.ws.WsServerConfig;

/**
 * @author WChao
 *
 */
public class ImConfig {
	
	private String bindIp = null;

	/**
	 * 监听端口
	 */
	private Integer bindPort = 80;
	/**
	 * 心跳包发送时长heartbeatTimeout/2
	 */
	private long heartbeatTimeout = 0;
	/**
	 * http相关配置;
	 */
	private HttpConfig httpConfig;
	/**
	 * websocket相关配置;
	 */
	private WsServerConfig wsServerConfig;
	/**
	 * 全局群组上下文;
	 */
	public static GroupContext groupContext;
	
	
	public ImConfig(String bindIp,Integer bindPort){
		this.bindIp = bindIp;
		this.bindPort = bindPort;
	}
	public String getBindIp() {
		return bindIp;
	}
	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}
	public Integer getBindPort() {
		return bindPort;
	}
	public void setBindPort(Integer bindPort) {
		this.bindPort = bindPort;
	}
	public long getHeartbeatTimeout() {
		return heartbeatTimeout;
	}
	public void setHeartbeatTimeout(long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}
	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}
	public WsServerConfig getWsServerConfig() {
		return wsServerConfig;
	}
	public void setWsServerConfig(WsServerConfig wsServerConfig) {
		this.wsServerConfig = wsServerConfig;
	}
}
