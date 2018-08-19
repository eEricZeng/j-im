/**
 * 
 */
package org.jim.server.demo;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.Const;
import org.jim.common.ImConfig;
import org.jim.common.packets.Command;
import org.jim.server.ImServerStarter;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.HandshakeReqHandler;
import org.jim.server.command.handler.LoginReqHandler;
import org.jim.server.demo.command.DemoWsHandshakeProcessor;
import org.jim.server.demo.init.HttpServerInit;
import org.jim.server.demo.listener.ImDemoGroupListener;
import org.jim.server.demo.service.LoginServiceProcessor;
import org.tio.core.ssl.SslConfig;
import com.jfinal.kit.PropKit;
/**
 * 
 * @author WChao
 *
 */
public class ImServerDemoStart {
	
	public static void main(String[] args)throws Exception{
		PropKit.use("app.properties");
		int port = PropKit.getInt("port");//启动端口
		ImConfig.isStore =  PropKit.get("is_store");//是否开启持久化;(on:开启,off:不开启)
		ImConfig.isCluster = PropKit.get("is_cluster");//是否开启集群;
		ImConfig imConfig = new ImConfig(null, port);
		HttpServerInit.init(imConfig);
		initSsl(imConfig);//初始化SSL;(开启SSL之前,你要保证你有SSL证书哦...)
		//ImgMnService.start();//启动爬虫爬取模拟在线人头像;
		imConfig.setImGroupListener(new ImDemoGroupListener());//设置群组监听器，非必须，根据需要自己选择性实现;
		ImServerStarter imServerStarter = new ImServerStarter(imConfig);
		/*****************start 以下处理器根据业务需要自行添加与扩展，每个Command都可以添加扩展,此处为demo中处理**********************************/
		HandshakeReqHandler handshakeReqHandler = CommandManager.getCommand(Command.COMMAND_HANDSHAKE_REQ, HandshakeReqHandler.class);
		handshakeReqHandler.addProcessor(new DemoWsHandshakeProcessor());//添加自定义握手处理器;
		LoginReqHandler loginReqHandler = CommandManager.getCommand(Command.COMMAND_LOGIN_REQ,LoginReqHandler.class);
		loginReqHandler.addProcessor(new LoginServiceProcessor());//添加登录业务处理器;
		/*****************end *******************************************************************************************/
		imServerStarter.start();
	}
	/**
	 * 开启SSL之前，你要保证你有SSL证书哦！
	 * @param imConfig
	 * @throws Exception
	 */
	private static void initSsl(ImConfig imConfig) throws Exception {
		ImConfig.isSSL = PropKit.get("is_ssl");//是否开启SSL;
		if(Const.ON.equals(ImConfig.isSSL)){//开启SSL
			String keyStorePath = PropKit.get("key_store_path");
			String keyStoreFile = keyStorePath;
			String trustStoreFile = keyStorePath;
			String keyStorePwd = PropKit.get("key_store_pwd");
			if (StringUtils.isNotBlank(keyStoreFile) && StringUtils.isNotBlank(trustStoreFile)) {
				SslConfig sslConfig = SslConfig.forServer(keyStoreFile, trustStoreFile, keyStorePwd);
				imConfig.setSslConfig(sslConfig);
			}
		}
	}
}
