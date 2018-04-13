/**
 * 
 */
package org.jim.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月26日 上午11:44:10
 */
public class UserReqBody extends Message{
	
	private static final long serialVersionUID = 1861307516710578262L;
	private String userid;//用户id;
	private Integer type;//0:单个,1:所有在线用户,2:所有用户(在线+离线);
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
