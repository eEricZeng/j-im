package org.jim.common.ws;

import org.jim.common.config.Config;
import org.jim.common.http.HttpConst;

/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年9月6日 上午11:11:26
 */
public class WsServerConfig extends Config{
	
	private String charset = HttpConst.CHARSET_NAME;
	
	private IWsMsgHandler wsMsgHandler;
	
	public WsServerConfig(){};

	public WsServerConfig(Integer bindPort) {
		this.bindPort = bindPort;
	}
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public IWsMsgHandler getWsMsgHandler() {
		return wsMsgHandler;
	}
	public void setWsMsgHandler(IWsMsgHandler wsMsgHandler) {
		this.wsMsgHandler = wsMsgHandler;
	}
}
