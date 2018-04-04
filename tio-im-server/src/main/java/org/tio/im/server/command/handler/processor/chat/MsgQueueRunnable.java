package org.tio.im.server.command.handler.processor.chat;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.command.handler.ChatReqHandler;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 * @author WChao
 * @date 2018年4月3日 上午10:47:40
 */
public class MsgQueueRunnable extends AbstractQueueRunnable<ChatBody> {
	
	private Logger log = LoggerFactory.getLogger(MsgQueueRunnable.class);
	
	private ChannelContext channelContext = null;
	
	private AbstractChatProcessor chatProCmdHandler;
	
	@Override
	public boolean addMsg(ChatBody msg) {
		if (this.isCanceled()) {
			log.error("{}, 任务已经取消，{}添加到消息队列失败", channelContext, msg);
			return false;
		}
		return msgQueue.add(msg);
	}

	public MsgQueueRunnable(ChannelContext channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
		ChatReqHandler chatReqHandler = CommandManager.getCommand(Command.COMMAND_CHAT_REQ,ChatReqHandler.class);
		chatProCmdHandler = (AbstractChatProcessor)chatReqHandler.getProcessor(channelContext);
	}

	@Override
	public void runTask() {
		int queueSize = msgQueue.size();
		if (queueSize == 0) {
			return;
		}
		ChatBody chatBody = null;
		while ((chatBody = msgQueue.poll()) != null) {
			if(chatProCmdHandler != null){
				try {
					chatProCmdHandler.handler(chatBody, channelContext);
				} catch (Exception e) {
					log.error(e.toString(),e);
				}
			}
		}
	}
}
