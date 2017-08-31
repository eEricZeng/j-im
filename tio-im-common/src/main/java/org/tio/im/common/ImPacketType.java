/**
 * 
 */
package org.tio.im.common;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月23日 上午10:59:35
 */
public enum ImPacketType {
	HTTP("http","Http协议包"),WS("ws","Websocket协议包"),TCP("tcp","Tcp协议包");
	private String value;
	private String description;
	
	private ImPacketType(String value,String description){
		this.value = value;
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
