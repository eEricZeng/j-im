package org.tio.im.common.packets;

/**
 * @author WChao
 * @date 2018年4月10日 下午3:18:06
 */
public class MessageReqBody extends Message {

	private static final long serialVersionUID = -4748178964168947701L;
	
	private String fromUserId;//发送用户id;
	private String userId;//接收用户id;
	private String groupId;//群组id;
	private Integer type;//0:离线消息,1:历史消息;
	
	public String getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}
