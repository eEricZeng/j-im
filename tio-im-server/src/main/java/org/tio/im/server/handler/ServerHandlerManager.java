/**
 * 
 */
package org.tio.im.server.handler;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImConfig;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午2:40:24
 */
@SuppressWarnings("unchecked")
public class ServerHandlerManager{
	
	private static Logger logger = Logger.getLogger(ServerHandlerManager.class);
	
	private static Map<String,AbServerHandler> serverHandlers = new HashMap<String,AbServerHandler>();
	
	static{
		 try {
			List<ServerHandlerConfiguration> configurations = ServerHandlerConfigurationFactory.parseConfiguration();
			init(configurations);
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
	}
	
	private static void init(List<ServerHandlerConfiguration> configurations) throws Exception{
		for(ServerHandlerConfiguration configuration : configurations){
			Class<AbServerHandler> serverHandlerClazz = (Class<AbServerHandler>)Class.forName(configuration.getServerHandler());
			AbServerHandler serverdHandler = serverHandlerClazz.newInstance();
			addServerHandler(serverdHandler);
		}
	}
	
	public static AbServerHandler addServerHandler(AbServerHandler serverHandler){
		if(serverHandler == null)
			return null;
		return serverHandlers.put(serverHandler.name(),serverHandler);
	}
	
	public static AbServerHandler removeServerHandler(String name){
		if(name == null || "".equals(name))
			return null;
		return serverHandlers.remove(name);
	}
	
	public static AbServerHandler getServerHandler(ByteBuffer buffer,ChannelContext channelContext){
		for(Entry<String,AbServerHandler> entry : serverHandlers.entrySet()){
			ByteBuffer copyByteBuffer = null;
			if(buffer != null && channelContext.getAttribute() == null){
				copyByteBuffer = ByteBuffer.wrap(buffer.array());
			}
			AbServerHandler serverHandler = entry.getValue();
			try {
				if(serverHandler.isProtocol(copyByteBuffer,channelContext)){
					return serverHandler;
				}
			} catch (Throwable e) {
				logger.error(e);
			}
		}
		return null;
	}
	
	public static <T> T getServerHandler(String name,Class<T> clazz){
		AbServerHandler serverHandler = serverHandlers.get(name);
		if(serverHandler == null)
			return null;
		return (T)serverHandler;
	}
	
	public static void init(ImConfig imConfig){
		for(Entry<String,AbServerHandler> entry : serverHandlers.entrySet()){
			try {
				entry.getValue().init(imConfig);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
