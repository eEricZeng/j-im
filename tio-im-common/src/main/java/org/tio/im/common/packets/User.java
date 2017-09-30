/**
 * 
 */
package org.tio.im.common.packets;

import java.util.List;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 下午3:13:47
 */
public class User{
	
	private String id;//用户id;
	private String nick; //user nick
	private String avatar; //用户头像
	private String status;//在线状态(online、offline)
	private String sign;//个性签名;
	private List<Group> friends;//好友列表;
	private List<Group> groups;//群组列表;
	
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public List<Group> getFriends() {
		return friends;
	}
	public void setFriends(List<Group> friends) {
		this.friends = friends;
	}
}
