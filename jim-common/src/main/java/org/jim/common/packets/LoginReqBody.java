/**
 * 
 */
package org.jim.common.packets;

import org.jim.common.packets.Message;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月12日 下午3:13:22
 */
public class LoginReqBody extends Message {
	
	private static final long serialVersionUID = -10113316720288444L;

	private String loginname;
	
	private String password;
	
	private String token;
	
	public LoginReqBody(){}
	
	public LoginReqBody(String token){
		this.token = token;
		this.cmd = Command.COMMAND_LOGIN_REQ.getNumber();
	}
	public LoginReqBody(String loginname,String password){
		this.loginname = loginname;
		this.password = password;
		this.cmd = Command.COMMAND_LOGIN_REQ.getNumber();
	}
	public LoginReqBody(String loginname,String password,String token){
		this(loginname,password);
		this.token = token;
	}
	public String getLoginname() {
		return loginname;
	}
	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
