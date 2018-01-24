/**
 * 
 */
package org.tio.im.server.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tio.core.ChannelContext;
import org.tio.im.server.command.handler.proc.ProCmdHandlerIntf;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午2:07:44
 */
public abstract class AbCmdHandler implements CmdHandlerIntf {
	
	//不同协议cmd处理命令如(ws、socket、自定义协议)握手、心跳命令等.
	protected Map<String,ProCmdHandlerIntf> proCmdHandlers = new HashMap<String,ProCmdHandlerIntf>();
	
	public AbCmdHandler addProcCmdHandler(ProCmdHandlerIntf proCmdHandler){
		this.proCmdHandlers.put(proCmdHandler.name(), proCmdHandler);
		return this;
	}
	
	public ProCmdHandlerIntf getProcCmdHandler(ChannelContext channelContext){
		for(Entry<String,ProCmdHandlerIntf> proCmdEntry : proCmdHandlers.entrySet()){
			ProCmdHandlerIntf proCmd = proCmdEntry.getValue();
			if(proCmd.isProtocol(channelContext)){
				return proCmd;
			}
		}
		return null;
	}
	
	public ProCmdHandlerIntf getProcCmdHandler(String name){
		for(Entry<String,ProCmdHandlerIntf> proCmdEntry : proCmdHandlers.entrySet()){
			ProCmdHandlerIntf proCmd = proCmdEntry.getValue();
			if(name.equals(proCmd.name())){
				return proCmd;
			}
		}
		return null;
	}
}
