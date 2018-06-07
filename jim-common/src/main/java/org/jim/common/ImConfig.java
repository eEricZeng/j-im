/**
 * 
 */
package org.jim.common;

import org.jim.common.cluster.ImCluster;
import org.jim.common.http.HttpConfig;
import org.jim.common.message.IMesssageHelper;
import org.jim.common.ws.WsServerConfig;
import org.tio.core.GroupContext;
import org.tio.core.intf.GroupListener;
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
	 * 群组监听器;
	 */
	private GroupListener imGroupListener;
	/**
	 * 用户消息持久化助手;
	 */
	private static IMesssageHelper messageHelper;
	/**
	 * 是否开启持久化;
	 */
	public static String isStore = "off";
	/**
	 * 是否开启集群;
	 */
	public static String isCluster = "off";
	/**
	 * 集群配置
	 * 如果此值不为null，就表示要集群
	 */
	public static ImCluster cluster;
	/**
	 *  默认的接收数据的buffer size
	 */
	private long readBufferSize = 1024 * 2;
	
	
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
	public GroupListener getImGroupListener() {
		return imGroupListener;
	}
	public void setImGroupListener(GroupListener imGroupListener) {
		this.imGroupListener = imGroupListener;
	}
}
