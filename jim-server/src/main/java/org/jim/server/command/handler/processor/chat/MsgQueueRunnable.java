package org.jim.server.command.handler.processor.chat;

import java.util.concurrent.Executor;

import org.jim.common.ImPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Command;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.ChatReqHandler;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 * @author WChao
 * @date 2018年4月3日 上午10:47:40
 */
public class MsgQueueRunnable extends AbstractQueueRunnable<ImPacket> {
	
	private Logger log = LoggerFactory.getLogger(MsgQueueRunnable.class);
	
	private ChannelContext channelContext = null;
	
	private AbstractChatProcessor chatProcessor;
	
	@Override
	public boolean addMsg(ImPacket msg) {
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
		chatProcessor = (AbstractChatProcessor)chatReqHandler.getProcessor(channelContext);
	}

	@Override
	public void runTask() {
		int queueSize = msgQueue.size();
		if (queueSize == 0) {
			return;
		}
		ImPacket packet = null;
		while ((packet = msgQueue.poll()) != null) {
			if(chatProcessor != null){
				try {
					chatProcessor.handler(packet, channelContext);
				} catch (Exception e) {
					log.error(e.toString(),e);
				}
			}
		}
	}
}
