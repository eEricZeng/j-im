/**
 * 
 */
package org.jim.common.packets;

import com.alibaba.fastjson.JSONObject;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 上午11:34:44
 */
public class ChatBody extends Message {
	
	private static final long serialVersionUID = 5731474214655476286L;
	/**
	 * 发送用户id;
	 */
	private String from;
	/**
	 * 目标用户id;
	 */
	private String to;
	/**
	 * 消息类型;(如：0:text、1:image、2:voice、3:vedio、4:music、5:news)
	 */
	private Integer msgType;
	/**
	 * 聊天类型;(如公聊、私聊)
	 */
	private Integer chatType;
	/**
	 * 消息内容;
	 */
	private String content;
	/**
	 * 消息发到哪个群组;
	 */
	private String group_id;
	
	private ChatBody(){}
	
	private ChatBody(String id , String from , String to , Integer msgType , Integer chatType , String content , String group_id , Integer cmd , Long createTime , JSONObject extras){
		this.id = id;
		this.from = from ;
		this.to = to;
		this.msgType = msgType;
		this.chatType = chatType;
		this.content = content;
		this.group_id = group_id;
		this.cmd = cmd;
		this.createTime = createTime;
		this.extras = extras;
	}
	
	public static ChatBody.Builder newBuilder(){
		return new ChatBody.Builder();
	}
	public String getFrom() {
		return from;
	}
	public ChatBody setFrom(String from) {
		this.from = from;
		return this;
	}
	public String getTo() {
		return to;
	}
	public ChatBody setTo(String to) {
		this.to = to;
		return this;
	}
	
	public Integer getMsgType() {
		return msgType;
	}
	public ChatBody setMsgType(Integer msgType) {
		this.msgType = msgType;
		return this;
	}
	public String getContent() {
		return content;
	}
	public ChatBody setContent(String content) {
		this.content = content;
		return this;
	}
	
	public String getGroup_id() {
		return group_id;
	}
	public ChatBody setGroup_id(String group_id) {
		this.group_id = group_id;
		return this;
	}
	public Integer getChatType() {
		return chatType;
	}
	public ChatBody setChatType(Integer chatType) {
		this.chatType = chatType;
		return this;
	}
	
	public static class Builder extends Message.Builder<ChatBody,ChatBody.Builder>{
		/**
		 * 来自user_id;
		 */
		private String from;
		/**
		 * 目标user_id;
		 */
		private String to;
		/**
		 * 消息类型;(如：0:text、1:image、2:voice、3:vedio、4:music、5:news)
		 */
		private Integer msgType;
		/**
		 * 聊天类型;(如公聊、私聊)
		 */
		private Integer chatType;
		/**
		 * 消息内容;
		 */
		private String content;
		/**
		 * 消息发到哪个群组;
		 */
		private String group_id;
		
		public Builder(){};
		
		public Builder setFrom(String from) {
			this.from = from;
			return this;
		}
		public Builder setTo(String to) {
			this.to = to;
			return this;
		}
		public Builder setMsgType(Integer msgType) {
			this.msgType = msgType;
			return this;
		}
		public Builder setChatType(Integer chatType) {
			this.chatType = chatType;
			return this;
		}
		public Builder setContent(String content) {
			this.content = content;
			return this;
		}
		public Builder setGroup_id(String group_id) {
			this.group_id = group_id;
			return this;
		}
		@Override
		protected Builder getThis() {
			return this;
		}
		@Override
		public ChatBody build(){
			return new ChatBody(this.id , this.from , this.to , this.msgType , this.chatType , this.content , this.group_id ,this.cmd , this.createTime , this.extras);
		}
	}
}
