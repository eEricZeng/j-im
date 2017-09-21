/**
 * 
 */
package org.tio.im.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月21日 下午1:54:04
 */
public class Group extends Message{
	
	private String name;//群组名称;

	public Group(){}
	public Group(String id , String name){
		this.id = id;
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
