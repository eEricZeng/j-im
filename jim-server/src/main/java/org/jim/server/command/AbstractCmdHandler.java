/**
 * 
 */
package org.jim.server.command;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.jim.common.ImConfig;
import org.jim.server.command.handler.processor.CmdProcessor;
import org.tio.core.ChannelContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 版本: [1.0]
 * 功能说明: 
 * @author: WChao 创建时间: 2017年9月11日 下午2:07:44
 */
public abstract class AbstractCmdHandler implements CmdHandler {
	/**
	 * 不同协议cmd处理命令如(ws、socket、自定义协议)握手、心跳命令等.
	 */
	protected Map<String,CmdProcessor> processors = new HashMap<String,CmdProcessor>();
	/**
	 * IM相关配置类
	 */
	protected ImConfig imConfig;
	
	public AbstractCmdHandler() {};
	
	public AbstractCmdHandler(ImConfig imConfig) {
		this.imConfig = imConfig;
	}

	public AbstractCmdHandler addProcessor(CmdProcessor processor){
		this.processors.put(processor.name(), processor);
		return this;
	}

	/**
	 * 根据当前通道所属协议获取cmd业务处理器
	 * @param channelContext
	 * @return
	 */
	public <T> List<T> getProcessor(ChannelContext channelContext,Class<T> clazz){
		List<T> processorList = null;
		for(Entry<String,CmdProcessor> processorEntry : processors.entrySet()){
			CmdProcessor processor = processorEntry.getValue();
			if(processor.isProtocol(channelContext)){
				if(CollectionUtils.isEmpty(processorList)){
					processorList = Lists.newArrayList();
				}
				processorList.add((T)processor);
			}
		}
		return processorList;
	}

	/**
	 * 根据cmdProcessor名字获取cmd业务处理器
	 * @param name
	 * @return
	 */
	public <T> List<T> getProcessor(String name, Class<T> clazz){
		List<T> processorList = null;
		for(Entry<String,CmdProcessor> processorEntry : processors.entrySet()){
			CmdProcessor processor = processorEntry.getValue();
			if(name.equals(processor.name())){
				if(CollectionUtils.isEmpty(processorList)){
					processorList = Lists.newArrayList();
				}
				processorList.add((T)processor);
			}
		}
		return processorList;
	}
	/**
	 * 获取不包含指定名字的cmdProcessor
	 * @param names
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getProcessorNotEqualName(Set<String> names, Class<T> clazz){
		List<T> processorList = null;
		for(Entry<String,CmdProcessor> processorEntry : processors.entrySet()){
			CmdProcessor processor = processorEntry.getValue();
			if(CollectionUtils.isEmpty(processorList)){
				processorList = Lists.newArrayList();
			}
			if(CollectionUtils.isEmpty(names)){
				processorList.add((T)processor);
			}else {
				if(!names.contains(processor.name())){
					processorList.add((T)processor);
				}
			}
		}
		return processorList;
	}
	public CmdProcessor removeProcessor(String name){
		
		return processors.remove(name);
	}
	
	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
	
}
