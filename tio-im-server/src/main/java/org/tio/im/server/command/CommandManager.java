/**
 * 
 */
package org.tio.im.server.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.handler.processor.ProcessorIntf;
/**
 * 版本: [1.0]
 * 功能说明: 命令执行管理器;
 * 作者: WChao 创建时间: 2017年7月17日 下午2:23:41
 */
@SuppressWarnings("unchecked")
public class CommandManager{
	
	private static  Map<Command, AbCmdHandler> handlerMap = new HashMap<>();//通用cmd处理命令
	private static Logger LOG = LoggerFactory.getLogger(CommandManager.class);
	
	private CommandManager(){};
	
	static{
		 try {
			List<CommandConfiguration> configurations = CommandConfigurationFactory.parseConfiguration();
			init(configurations);
		} catch (Exception e) {
			LOG.error(e.toString(),e);
		}
	}
	
	private static void init(List<CommandConfiguration> configurations) throws Exception{
		for(CommandConfiguration configuration : configurations){
			Class<AbCmdHandler> cmdHandlerClazz = (Class<AbCmdHandler>)Class.forName(configuration.getCmdHandler());
			AbCmdHandler cmdHandler = cmdHandlerClazz.newInstance();
			List<String> proCmdHandlerList = configuration.getProCmdhandlers();
			if(!proCmdHandlerList.isEmpty()){
				for(String proCmdHandlerClass : proCmdHandlerList){
					Class<ProcessorIntf> proCmdHandlerClazz = (Class<ProcessorIntf>)Class.forName(proCmdHandlerClass);
					ProcessorIntf proCmdHandler = proCmdHandlerClazz.newInstance();
					cmdHandler.addProcCmdHandler(proCmdHandler);
				}
			}
			registerCommand(cmdHandler);
		}
	}
	public static AbCmdHandler registerCommand(AbCmdHandler imCommandHandler) throws Exception{
		if(imCommandHandler == null || imCommandHandler.command() == null)
			return null;
		int cmd_number = imCommandHandler.command().getNumber();
		if(Command.forNumber(cmd_number) == null)
			throw new Exception("注册cmd处理器失败,不合法的cmd命令码:"+cmd_number+",请在Command枚举类中添加!");
		if(handlerMap.get(imCommandHandler.command()) == null)
		{
			return handlerMap.put(imCommandHandler.command(),imCommandHandler);
		}
		return null;
	}
	
	public static AbCmdHandler removeCommand(Command command){
		if(command == null)
			return null;
		if(handlerMap.get(command) != null)
		{
			return handlerMap.remove(command);
		}
		return null;
	}
	
	public static <T> T getCommand(Command command,Class<T> clazz){
		AbCmdHandler cmdHandler = getCommand(command);
		if(cmdHandler != null){
			return (T)cmdHandler;
		}
		return null;
	}
	
	public static AbCmdHandler getCommand(Command command){
		if(command == null)
			return null;
		
		return handlerMap.get(command);
	}
}
