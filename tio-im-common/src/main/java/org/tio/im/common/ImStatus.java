/**
 * 
 */
package org.tio.im.common;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月27日 上午10:33:14
 */
public enum ImStatus implements Status{
	
	C0(1,"offline","用户不在线"),
	C1(0,"ok","发送成功"),
	C2(2,"send failed","消息发送失败,数据格式不正确,请参考:{'from':来源ID,'to':目标ID,'cmd':'消息命令码','createTime':消息创建时间(Long型),'msgType':消息类型,'content':内容}"),
	C100(100,"ok","获取用户信息成功!"),
	C101(101,"get user failed !","获取用户信息失败!"),
	C200(200,"ok","登录成功!"),
	C201(201,"login failed !","登录失败!"),
	C300(300,"ok","鉴权成功!"),
	C301(301,"auth failed!","鉴权失败!"),
	C400(400,"join group ok!","加入群组成功!"),
	C401(401,"join group failed!","加入群组失败!");
	
	private int status;
	
	private String description;
	
	private String text;

	private ImStatus(int status, String description, String text) {
		this.status = status;
		this.description = description;
		this.text = text;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public int getCode() {
		return this.status;
	}

	@Override
	public String getMsg() {
		return this.getStatus()+" "+this.getDescription()+" "+this.getText();
	}
}
