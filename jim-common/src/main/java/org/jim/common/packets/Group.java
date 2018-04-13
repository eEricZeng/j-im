/**
 * 
 */
package org.jim.common.packets;

import java.util.List;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月21日 下午1:54:04
 */
public class Group extends Message{
	
	private static final long serialVersionUID = -3817755433171220952L;
	private String group_id;
	private String name;//群组名称;
	private String avatar;//群组头像;
	private Integer online;//在线人数;
	private List<User> users;//组用户;

	public Group(){}
	public Group(String group_id , String name){
		this.group_id = group_id;
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Integer getOnline() {
		return online;
	}
	public void setOnline(Integer online) {
		this.online = online;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
}
