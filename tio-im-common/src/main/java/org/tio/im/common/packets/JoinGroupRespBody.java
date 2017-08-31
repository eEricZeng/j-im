/**
 * 
 */
package org.tio.im.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 加入群组响应
 * 作者: WChao 创建时间: 2017年7月26日 下午5:09:20
 */
public class JoinGroupRespBody {
	public JoinGroupResult result;
	public String group;
	public JoinGroupResult getResult() {
		return result;
	}
	public void setResult(JoinGroupResult result) {
		this.result = result;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
}
