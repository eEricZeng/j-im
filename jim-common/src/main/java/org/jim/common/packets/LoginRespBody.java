/**
 * 
 */
package org.jim.common.packets;

import org.jim.common.Status;
import org.jim.common.packets.User;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月12日 下午3:15:28
 */
public class LoginRespBody extends RespBody {
	
	private static final long serialVersionUID = 1L;
	
	private String token;
	private User user;

	public LoginRespBody(Command command , Status status){
		this(command,status,null);
	}

	public LoginRespBody(Command command , Status status , User user){
		super(command, status);
		this.user = user;
	}
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
	@Override
	public void clear() {
		setToken(null);
		setUser(null);
	}
}
