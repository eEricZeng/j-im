/**
 * 
 */
package org.tio.im.common.bs;

/**
 * 
 * @author tanyaowu 
 */

public class JoinNotifyRespBody extends org.tio.im.common.bs.BaseRespBody
{
	private String group;
	private Integer userid;
	private String nick;
	
	private Integer allcount;   //所有客户端数(包括注册和没注册的)
	private Integer usercount;   //注册用户数
	
	
	public JoinNotifyRespBody()
	{
		
	}

	public String getGroup() {
		return group;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public Integer getUserid() {
		return userid;
	}


	public void setUserid(Integer userid) {
		this.userid = userid;
	}


	public String getNick() {
		return nick;
	}


	public void setNick(String nick) {
		this.nick = nick;
	}


	public Integer getAllcount() {
		return allcount;
	}


	public void setAllcount(Integer allcount) {
		this.allcount = allcount;
	}


	public Integer getUsercount() {
		return usercount;
	}


	public void setUsercount(Integer usercount) {
		this.usercount = usercount;
	}

}
