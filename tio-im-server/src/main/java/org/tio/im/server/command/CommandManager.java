/**
 * 
 */
package org.tio.im.server.command;

import java.util.HashMap;
import java.util.Map;

import org.tio.im.common.packets.Command;
/**
 * 版本: [1.0]
 * 功能说明: 命令执行管理器;
 * 作者: WChao 创建时间: 2017年7月17日 下午2:23:41
 */
public class CommandManager{
	
	private  Map<Command, CmdHandler> handlerMap = new HashMap<>();//通用cmd处理命令
	
	private CommandManager(){};
	private static CommandManager instance = null;
	
	public static CommandManager getInstance(){
		if(instance == null){
			synchronized (CommandManager.class) {
				if(instance == null){
					instance = new CommandManager();
				}
			}
		}
		return instance;
	}
	
	public CommandManager registerCommand(CmdHandler imCommandHandler){
		if(handlerMap.get(imCommandHandler.command()) == null)
		{
			handlerMap.put(imCommandHandler.command(),imCommandHandler);
		}
		return this;
	}
	
	public CommandManager removeCommand(Command command){
		if(handlerMap.get(command) != null)
		{
			handlerMap.remove(command);
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getCommand(Command command,Class<T> clazz){
		CmdHandler cmdHandler = this.getCommand(command);
		if(cmdHandler != null){
			return (T)cmdHandler;
		}
		return null;
	}
	
	public CmdHandler getCommand(Command command){
		if(command == null)
			return null;
		
		return handlerMap.get(command);
	}
}
