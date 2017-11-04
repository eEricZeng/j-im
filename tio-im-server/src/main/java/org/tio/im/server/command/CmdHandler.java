/**
 * 
 */
package org.tio.im.server.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tio.core.ChannelContext;
import org.tio.im.server.command.handler.proc.ProCmdIntf;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午2:07:44
 */
public abstract class CmdHandler implements BsCmdHandlerIntf {
	
	//不同协议cmd处理命令如(ws、socket、自定义协议)握手、心跳命令等.
	protected Map<String,ProCmdIntf> proCmdHandlers = new HashMap<String,ProCmdIntf>();
	
	public CmdHandler addProcCmdHandler(ProCmdIntf proCmdHandler){
		this.proCmdHandlers.put(proCmdHandler.name(), proCmdHandler);
		return this;
	}
	
	public ProCmdIntf getProcCmdHandler(ChannelContext channelContext){
		for(Entry<String,ProCmdIntf> proCmdEntry : proCmdHandlers.entrySet()){
			ProCmdIntf proCmd = proCmdEntry.getValue();
			if(proCmd.isProtocol(channelContext)){
				return proCmd;
			}
		}
		return null;
	}
	
	public ProCmdIntf getProcCmdHandler(String name){
		for(Entry<String,ProCmdIntf> proCmdEntry : proCmdHandlers.entrySet()){
			ProCmdIntf proCmd = proCmdEntry.getValue();
			if(name.equals(proCmd.name())){
				return proCmd;
			}
		}
		return null;
	}
}
