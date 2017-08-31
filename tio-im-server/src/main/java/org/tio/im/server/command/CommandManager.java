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
	private  Map<Command, ImBsHandlerIntf> handlerMap = new HashMap<>();
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
	
	public CommandManager registerCommand(ImBsHandlerIntf imCommandHandler){
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
	
	public ImBsHandlerIntf getCommand(Command command){
		if(command == null)
			return null;
		
		return handlerMap.get(command);
	}
	
	@SuppressWarnings("deprecation")
	public Command getCommand(int cmd){
		return Command.valueOf(cmd);
	}
	public Command getCommand(String cmd){
		if(cmd == null)
			return null;
		int cmd_Int = Integer.parseInt(cmd);
		return this.getCommand(cmd_Int);
	}
}
