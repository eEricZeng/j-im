/**
 * 
 */
package org.tio.im.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 上午11:34:44
 */
public class ChatBody extends Message {
	
	private String from;//来自channel id;
	private String to;//目标channel id;
	private Integer msgType;//消息类型;(如：0:text、1:image、2:voice、3:vedio、4:music、5:news)
	private Integer chatType;//聊天类型;(如公聊、私聊)
	private String content;//消息内容;
	private String group_id;//消息发到哪个群组;
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
	
}
