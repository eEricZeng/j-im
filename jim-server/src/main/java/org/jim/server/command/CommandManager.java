/**
 * 
 */
package org.jim.server.command;

import org.jim.common.ImConfig;
import org.jim.common.packets.Command;
import org.jim.server.command.handler.processor.CmdProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 版本: [1.0]
 * 功能说明: 命令执行管理器;
 * @author : WChao 创建时间: 2017年7月17日 下午2:23:41
 */
public class CommandManager{
	/**
	 * 通用cmd处理命令
	 */
	private static  Map<Integer, AbstractCmdHandler> handlerMap = new HashMap<>();
	private static Logger LOG = LoggerFactory.getLogger(CommandManager.class);
	
	private static ImConfig imConfig;
	
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
			Class<AbstractCmdHandler> cmdHandlerClazz = (Class<AbstractCmdHandler>)Class.forName(configuration.getCmdHandler());
			AbstractCmdHandler cmdHandler = cmdHandlerClazz.newInstance();
			List<String> proCmdHandlerList = configuration.getProCmdhandlers();
			if(!proCmdHandlerList.isEmpty()){
				for(String proCmdHandlerClass : proCmdHandlerList){
					Class<CmdProcessor> proCmdHandlerClazz = (Class<CmdProcessor>)Class.forName(proCmdHandlerClass);
					CmdProcessor proCmdHandler = proCmdHandlerClazz.newInstance();
					cmdHandler.addProcessor(proCmdHandler);
				}
			}
			registerCommand(cmdHandler);
		}
	}
	public static AbstractCmdHandler registerCommand(AbstractCmdHandler imCommandHandler) throws Exception{
		if(imCommandHandler == null || imCommandHandler.command() == null) {
			return null;
		}
		int cmd_number = imCommandHandler.command().getNumber();
		if(Command.forNumber(cmd_number) == null) {
			throw new Exception("注册cmd处理器失败,不合法的cmd命令码:" + cmd_number + ",请在Command枚举类中添加!");
		}
		if(handlerMap.get(cmd_number) == null)
		{
			imCommandHandler.setImConfig(imConfig);
			return handlerMap.put(cmd_number,imCommandHandler);
		}
		return null;
	}
	
	public static AbstractCmdHandler removeCommand(Command command){
		if(command == null) {
			return null;
		}
		int cmd_value = command.getNumber();
		if(handlerMap.get(cmd_value) != null)
		{
			return handlerMap.remove(cmd_value);
		}
		return null;
	}
	
	public static <T> T getCommand(Command command,Class<T> clazz){
		AbstractCmdHandler cmdHandler = getCommand(command);
		if(cmdHandler != null){
			return (T)cmdHandler;
		}
		return null;
	}
	
	public static AbstractCmdHandler getCommand(Command command){
		if(command == null) {
			return null;
		}
		return handlerMap.get(command.getNumber());
	}
	
	public static void init(ImConfig config) {
		imConfig = config;
		for(Entry<Integer,AbstractCmdHandler> entry : handlerMap.entrySet()){
			try {
				entry.getValue().setImConfig(imConfig);
			} catch (Exception e) {
				LOG.error(e.toString());
			}
		}
	}
	public static ImConfig getImConfig() {
		return imConfig;
	}
}
