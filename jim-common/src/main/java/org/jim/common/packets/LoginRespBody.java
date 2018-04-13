/**
 * 
 */
package org.jim.common.packets;

import org.jim.common.packets.Message;
import org.jim.common.packets.User;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月12日 下午3:15:28
 */
public class LoginRespBody extends Message {
	
	private static final long serialVersionUID = 2712935588211143034L;
	private String token;
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
