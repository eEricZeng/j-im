/**
 * 
 */
package org.tio.im.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 进入群组通知消息体
 * 作者: WChao 创建时间: 2017年7月26日 下午5:14:04
 */
public class JoinGroupNotifyRespBody {
	private Client client;
	private String group;
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
}
