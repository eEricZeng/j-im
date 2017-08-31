package org.tio.im.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.server.handler.ImServerAioHandler;
import org.tio.im.server.listener.ImGroupListener;
import org.tio.im.server.listener.ImServerAioListener;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;

import com.jfinal.kit.PropKit;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
/**
 * 
 * @author tanyaowu 
 *
 */
public class ImServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImServerStarter.class);

	public static Config conf = ConfigFactory.load("app.properties");

	/**
	 * 
	 * @author: tanyaowu
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public ImServerStarter() {

	}

	static ImServerAioHandler aioHandler = null ;
	static ImServerAioListener aioListener = null;
	static ImGroupListener imGroupListener = new ImGroupListener();
	static ServerGroupContext serverGroupContext = null;
	static AioServer aioServer = null;
	static String bindIp = null;//"127.0.0.1";

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public static void main(String[] args) throws Exception {
		start();
	}

	public static void start() throws Exception{
		PropKit.use("app.properties");
		int port = PropKit.getInt("port");//启动端口
		aioHandler = new ImServerAioHandler();
		aioHandler.init();
		aioListener = new ImServerAioListener();
		serverGroupContext = new ServerGroupContext(aioHandler, aioListener);
		aioServer = new AioServer(serverGroupContext);
		
		serverGroupContext.setGroupListener(imGroupListener);
		serverGroupContext.setHeartbeatTimeout(0);
		//serverGroupContext.setShortConnection(true);
		aioServer.start(bindIp, port);
	}
}
