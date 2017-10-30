/**
 * 
 */
package org.tio.im.server.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;

/**
 * 版本: [1.0]
 * 功能说明: 命令执行管理器;
 * 作者: WChao 创建时间: 2017年7月17日 下午2:23:41
 */
public class CommandManager{
	
	private Logger log = LoggerFactory.getLogger(CommandManager.class);
	private  Map<Command, CmdHandler> handlerMap = new HashMap<>();//通用cmd处理命令
	private List<ProCmdHandlerIntf> proCmdHandlers = new ArrayList<ProCmdHandlerIntf>();//不同协议cmd处理命令如(ws、socket、自定义协议)握手心跳命令等.
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
	public CommandManager addProCmdHandler(ProCmdHandlerIntf proCmdHandler){
		this.proCmdHandlers.add(proCmdHandler);
		return this;
	}
	
	public CmdHandler getCommand(Command command){
		if(command == null)
			return null;
		
		return handlerMap.get(command);
	}
	
	public ProCmdHandlerIntf getProCmdHandler(ChannelContext channelContext){
		for(ProCmdHandlerIntf handler : proCmdHandlers){
			try {
				if(handler.isProtocol(channelContext)){
					return handler;
				}
			} catch (Exception e) {
				log.error(e.toString(),e);
			}
		}
		return null;
	}
}
