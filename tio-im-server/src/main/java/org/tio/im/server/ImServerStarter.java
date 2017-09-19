package org.tio.im.server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.server.handler.ImServerAioHandler;
import org.tio.im.server.listener.ImGroupListener;
import org.tio.im.server.listener.ImServerAioListener;
import org.tio.im.server.service.ImgMnService;
import org.tio.server.AioServer;
import com.jfinal.kit.PropKit;
/**
 * 
  * @ClassName: ImServerStarter  
  * @Description: TODO(消息服务端启动入口)  
  * @author WChao wchaojava@163.com  
  * @date 2017年9月3日 下午11:32:02  
  *
 */
public class ImServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(ImServerStarter.class);

	/**
	 * 
	  * @author WChao wchaojava@163.com  
	  * @date 2017年9月3日 下午11:34:58
	 */
	public ImServerStarter() {

	}

	static ImServerAioHandler aioHandler = new ImServerAioHandler() ;
	static ImServerAioListener aioListener = new ImServerAioListener();
	static ImGroupListener imGroupListener = new ImGroupListener();
	static ImServerGroupContext serverGroupContext = new ImServerGroupContext(aioHandler, aioListener);
	static AioServer aioServer = null;
	static String bindIp = null;//"127.0.0.1";

	/**
	 * 
	* @author WChao wchaojava@163.com  
	* @date 2017年9月3日 下午11:36:37 
	* @Description: TODO(这里用一句话描述这个方法的作用)  
	* @param @param args
	* @param @throws Exception    设定文件  
	* @return void    返回类型  
	* @throws
	 */
	public static void main(String[] args) throws Exception {
		start();
		ImgMnService.start();//启动头像爬虫;
	}

	public static void start() throws Exception{
		PropKit.use("app.properties");
		int port = PropKit.getInt("port");//启动端口
		serverGroupContext.setGroupListener(imGroupListener);
		serverGroupContext.setHeartbeatTimeout(0);
		//serverGroupContext.setShortConnection(true);
		aioServer = new AioServer(serverGroupContext);
		aioServer.start(bindIp, port);
	}
}
