/**
 * 
 */
package org.jim.common;

import org.tio.core.GroupContext;
import org.jim.common.http.HttpConfig;
import org.jim.common.message.IMesssageHelper;
import org.jim.common.ws.WsServerConfig;
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
	/**
	 * 用户消息持久化助手;
	 */
	private static IMesssageHelper messageHelper;
	/**
	 * 是否开启持久化;
	 */
	public static String isStore;
	/**
	 *  默认的接收数据的buffer size
	 */
	private long readBufferSize = 1024 * 1024;
	
	
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
	public static IMesssageHelper getMessageHelper() {
		return messageHelper;
	}
	public static void setMessageHelper(IMesssageHelper helper) {
		messageHelper = helper;
	}
	public long getReadBufferSize() {
		return readBufferSize;
	}
	public void setReadBufferSize(long readBufferSize) {
		this.readBufferSize = readBufferSize;
	}
	
}
