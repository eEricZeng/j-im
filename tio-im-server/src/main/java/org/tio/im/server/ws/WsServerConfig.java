package org.tio.im.server.ws;

import org.tio.im.common.http.HttpConst;

/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月6日 上午11:11:26
 */
public class WsServerConfig {
	
	private String bindIp = null;//"127.0.0.1";
	
	private Integer bindPort = 9322;
	
	private String charset = HttpConst.CHARSET_NAME;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public WsServerConfig(Integer bindPort) {
		
		this.bindPort = bindPort;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	

	/**
	 * @return the bindIp
	 */
	public String getBindIp() {
		return bindIp;
	}

	/**
	 * @param bindIp the bindIp to set
	 */
	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	/**
	 * @return the bindPort
	 */
	public Integer getBindPort() {
		return bindPort;
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



}
