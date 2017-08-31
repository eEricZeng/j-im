/**
 * 
 */
package org.tio.im.common.packets;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 上午11:31:48
 */
public class ChatRespBody extends ChatBody{
	
	private Integer errorCode;
	
	private String errorMsg;

	public Integer getErrorCode() {
		return errorCode;
	}

	public ChatRespBody setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public ChatRespBody setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
		return this;
	}

	
}
