/**
 * 
 */
package org.tio.im.server.demo;

import org.tio.im.common.ImConfig;
import org.tio.im.common.packets.Command;
import org.tio.im.server.ImServerStarter;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.command.handler.HandshakeReqHandler;
import org.tio.im.server.command.handler.LoginReqHandler;
import org.tio.im.server.demo.command.WsDemoHandshakeProCmdHandler;
import org.tio.im.server.demo.init.HttpServerInit;
import org.tio.im.server.demo.listener.ImDemoAioListener;
import org.tio.im.server.demo.service.UserServiceHandler;

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
		ImConfig imConfig = new ImConfig("127.0.0.1", port);
		HttpServerInit.init(imConfig);
		ImServerStarter imServerStarter = new ImServerStarter(imConfig,new ImDemoAioListener());
		HandshakeReqHandler handshakeReqHandler = CommandManager.getInstance().getCommand(Command.COMMAND_HANDSHAKE_REQ,HandshakeReqHandler.class);
		handshakeReqHandler.addProcCmdHandler(new WsDemoHandshakeProCmdHandler());//添加自定义握手处理器;
		LoginReqHandler loginReqHandler = CommandManager.getInstance().getCommand(Command.COMMAND_LOGIN_REQ,LoginReqHandler.class);
		loginReqHandler.addProcCmdHandler(new UserServiceHandler());//添加登录业务处理器;
		imServerStarter.start();
	}
}
