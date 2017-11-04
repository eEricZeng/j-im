/**
 * 
 */
package org.tio.im.server;

import java.io.IOException;

import org.tio.im.common.ImConfig;
import org.tio.im.server.handler.ImServerAioHandler;
import org.tio.im.server.listener.ImGroupListener;
import org.tio.im.server.listener.ImServerAioListener;
import org.tio.server.AioServer;

/**
 * 
 * @author WChao
 *
 */
public class ImServerStarter {
	
	private ImServerAioHandler imAioHandler = null;
	private ImServerAioListener imAioListener = null;
	private ImServerGroupContext imServerGroupContext = null;
	private ImGroupListener imGroupListener = null;
	private AioServer aioServer = null;
	private ImConfig imConfig = null;
	
	public ImServerStarter(ImConfig imConfig){
		this(imConfig,null);
	}
	public ImServerStarter(ImConfig imConfig,ImServerAioListener imAioListener){
		this.imConfig = imConfig;
		this.imAioListener = imAioListener;
		init();
	}
	public void init(){
		imAioHandler = new ImServerAioHandler() ;
		if(imAioListener == null){
			imAioListener = new ImServerAioListener();
		}
		imGroupListener = new ImGroupListener();
		imServerGroupContext = new ImServerGroupContext(imConfig,imAioHandler, imAioListener);
		aioServer = new AioServer(imServerGroupContext);
		imServerGroupContext.setGroupListener(imGroupListener);
		imServerGroupContext.setHeartbeatTimeout(this.imConfig.getHeartbeatTimeout());
	}
	
	public void start() throws IOException {
		aioServer.start(this.imConfig.getBindIp(),this.imConfig.getBindPort());
	}
}
