package org.jim.server.command.handler.processor.chat;

import org.jim.common.ImConst;
import org.jim.common.ImConfig;
import org.jim.common.message.MessageHelper;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.ChatType;
import org.jim.common.utils.ChatKit;
import org.jim.server.command.CommandManager;
import org.tio.core.ChannelContext;

import java.util.List;
/**
 * @author WChao
 * @date 2018年4月3日 下午1:13:32
 */
public abstract class BaseAsyncChatMessageProcessor implements AsyncChatMessageProcessor,ImConst {
	
	protected ImConfig imConfig = null;
	/**
	 * 供子类拿到消息进行业务处理(如:消息持久化到数据库等)的抽象方法
	 * @param chatBody
	 * @param channelContext
	 */
    protected abstract void doHandler(ChatBody chatBody, ChannelContext channelContext);

	@Override
	public boolean isProtocol(ChannelContext channelContext) {
		return true;
	}

	@Override
	public String name() {
		return BASE_ASYNC_CHAT_MESSAGE_PROCESSOR;
	}

	@Override
	public void handler(ChatBody chatBody, ChannelContext channelContext){
		if(imConfig == null) {
			imConfig = CommandManager.getImConfig();
		}
		//开启持久化
		if(ON.equals(imConfig.getIsStore())){
			//存储群聊消息;
			if(ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()){
				pushGroupMessages(PUSH,STORE, chatBody);
			}else{
				String from = chatBody.getFrom();
				String to = chatBody.getTo();
				String sessionId = ChatKit.sessionId(from,to);
				writeMessage(STORE,USER+":"+sessionId,chatBody);
				boolean isOnline = ChatKit.isOnline(to,imConfig);
				if(!isOnline){
					writeMessage(PUSH,USER+":"+to+":"+from,chatBody);
				}
			}
		}
		doHandler(chatBody, channelContext);
	}
	/**
	 * 推送持久化群组消息
	 * @param pushTable
	 * @param storeTable
	 * @param chatBody
	 */
	private void pushGroupMessages(String pushTable, String storeTable , ChatBody chatBody){
		MessageHelper messageHelper = imConfig.getMessageHelper();
		String group_id = chatBody.getGroup_id();
		//先将群消息持久化到存储Timeline;
		writeMessage(storeTable,GROUP+":"+group_id,chatBody);
		List<String> userIds = messageHelper.getGroupUsers(group_id);
		//通过写扩散模式将群消息同步到所有的群成员
		for(String userId : userIds){
			boolean isOnline = false;
			if(ON.equals(imConfig.getIsStore()) && ON.equals(imConfig.getIsCluster())){
				isOnline = messageHelper.isOnline(userId);
			}else{
				isOnline = ChatKit.isOnline(userId,imConfig);
			}
			if(!isOnline){
				writeMessage(pushTable, GROUP+":"+group_id+":"+userId, chatBody);
			}
		}
	}
	
	private void writeMessage(String timelineTable , String timelineId , ChatBody chatBody){
		MessageHelper messageHelper = imConfig.getMessageHelper();
		messageHelper.writeMessage(timelineTable, timelineId, chatBody);
	}
}
