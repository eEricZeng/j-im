/**
 * 
 */
package org.jim.server.handler;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jim.common.ImConfig;
import org.jim.common.ImSessionContext;
import org.jim.common.protocol.IProtocol;
import org.jim.common.utils.ImKit;
import org.tio.core.ChannelContext;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午2:40:24
 */
@SuppressWarnings("unchecked")
public class ProtocolHandlerManager{
	
	private static Logger logger = Logger.getLogger(ProtocolHandlerManager.class);
	
	private static Map<String,AbProtocolHandler> serverHandlers = new HashMap<String,AbProtocolHandler>();
	
	static{
		 try {
			List<ProtocolHandlerConfiguration> configurations = ProtocolHandlerConfigurationFactory.parseConfiguration();
			init(configurations);
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
	}
	
	private static void init(List<ProtocolHandlerConfiguration> configurations) throws Exception{
		for(ProtocolHandlerConfiguration configuration : configurations){
			Class<AbProtocolHandler> serverHandlerClazz = (Class<AbProtocolHandler>)Class.forName(configuration.getServerHandler());
			AbProtocolHandler serverdHandler = serverHandlerClazz.newInstance();
			addServerHandler(serverdHandler);
		}
	}
	
	public static AbProtocolHandler addServerHandler(AbProtocolHandler serverHandler){
		if(serverHandler == null)
			return null;
		return serverHandlers.put(serverHandler.protocol().name(),serverHandler);
	}
	
	public static AbProtocolHandler removeServerHandler(String name){
		if(name == null || "".equals(name))
			return null;
		return serverHandlers.remove(name);
	}
	
	public static AbProtocolHandler initServerHandlerToChannelContext(ByteBuffer buffer,ChannelContext channelContext){
		IProtocol protocol = ImKit.protocol(buffer, channelContext);
		for(Entry<String,AbProtocolHandler> entry : serverHandlers.entrySet()){
			AbProtocolHandler protocolHandler = entry.getValue();
			String protoc_name = protocolHandler.protocol().name();
			try {
				if(protocol != null && protocol.name().equals(protoc_name)){
					ImSessionContext sessionContext = (ImSessionContext)channelContext.getAttribute();
					sessionContext.setProtocolHandler(protocolHandler);
					channelContext.setAttribute(sessionContext);
					return protocolHandler;
				}
			} catch (Throwable e) {
				logger.error(e);
			}
		}
		return null;
	}
	
	public static <T> T getServerHandler(String name,Class<T> clazz){
		AbProtocolHandler serverHandler = serverHandlers.get(name);
		if(serverHandler == null)
			return null;
		return (T)serverHandler;
	}
	
	public static void init(ImConfig imConfig){
		for(Entry<String,AbProtocolHandler> entry : serverHandlers.entrySet()){
			try {
				entry.getValue().init(imConfig);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
