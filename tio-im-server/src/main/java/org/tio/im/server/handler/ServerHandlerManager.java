/**
 * 
 */
package org.tio.im.server.handler;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.server.ServerGroupContext;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午2:40:24
 */
public class ServerHandlerManager{
	
	private Logger logger = Logger.getLogger(ServerHandlerManager.class);
	
	private static ServerHandlerManager instance = null;
	private Map<String,AbServerHandler> serverHandlers = new HashMap<String,AbServerHandler>();
	
	public static ServerHandlerManager getInstance(){
		if(instance == null){
			synchronized (ServerHandlerManager.class) {
				if(instance == null){
					instance = new ServerHandlerManager();
				}
			}
		}
		return instance;
	}
	
	public ServerHandlerManager addServerHandler(AbServerHandler serverHandler){
		if(serverHandler == null)
			return null;
		serverHandlers.put(serverHandler.name(),serverHandler);
		return this;
	}
	
	public AbServerHandler getServerHandler(ByteBuffer buffer,ChannelContext channelContext){
		for(Entry<String,AbServerHandler> entry : serverHandlers.entrySet()){
			ByteBuffer copyByteBuffer = null;
			if(buffer != null){
				copyByteBuffer = ByteBuffer.wrap(buffer.array());
			}
			AbServerHandler serverHandler = entry.getValue();
			try {
				if(serverHandler.isProtocol(copyByteBuffer,channelContext)){
					return serverHandler.build();
				}
			} catch (Throwable e) {
				logger.error(e);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getServerHandler(String name,Class<T> clazz){
		AbServerHandler serverHandler = serverHandlers.get(name);
		if(serverHandler == null)
			return null;
		return (T)serverHandler;
	}
	
	public ServerHandlerManager init(ServerGroupContext serverGroupContext){
		for(Entry<String,AbServerHandler> entry : serverHandlers.entrySet()){
			try {
				entry.getValue().init(serverGroupContext);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return this;
	}
}
