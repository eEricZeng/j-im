/**
 * 
 */
package org.jim.server.handler;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jim.common.ImConfig;
import org.jim.common.ImSessionContext;
import org.jim.common.exception.ImException;
import org.jim.common.protocol.IProtocol;
import org.jim.common.utils.ImKit;
import org.tio.core.ChannelContext;
/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月3日 下午2:40:24
 */
public class ProtocolHandlerManager{
	
	private static Logger logger = Logger.getLogger(ProtocolHandlerManager.class);
	
	private static Map<String,AbstractProtocolHandler> serverHandlers = new HashMap<String,AbstractProtocolHandler>();
	
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
			Class<AbstractProtocolHandler> serverHandlerClazz = (Class<AbstractProtocolHandler>)Class.forName(configuration.getServerHandler());
			AbstractProtocolHandler serverHandler = serverHandlerClazz.newInstance();
			addServerHandler(serverHandler);
		}
	}
	
	public static AbstractProtocolHandler addServerHandler(AbstractProtocolHandler serverHandler)throws ImException{
		if(Objects.isNull(serverHandler)){
			throw new ImException("ProtocolHandler must not null ");
		}
		return serverHandlers.put(serverHandler.protocol().name(),serverHandler);
	}
	
	public static AbstractProtocolHandler removeServerHandler(String name)throws ImException{
		if(StringUtils.isEmpty(name)){
			throw new ImException("server name must not empty");
		}
		return serverHandlers.remove(name);
	}
	
	public static AbstractProtocolHandler initServerHandlerToChannelContext(ByteBuffer buffer, ChannelContext channelContext){
		IProtocol protocol = ImKit.protocol(buffer, channelContext);
		for(Entry<String,AbstractProtocolHandler> entry : serverHandlers.entrySet()){
			AbstractProtocolHandler protocolHandler = entry.getValue();
			String protocolName = protocolHandler.protocol().name();
			try {
				if(protocol != null && protocol.name().equals(protocolName)){
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
		AbstractProtocolHandler serverHandler = serverHandlers.get(name);
		if(Objects.isNull(serverHandler)) {
			return null;
		}
		return (T)serverHandler;
	}
	
	public static void init(ImConfig imConfig){
		for(Entry<String,AbstractProtocolHandler> entry : serverHandlers.entrySet()){
			try {
				entry.getValue().init(imConfig);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}
