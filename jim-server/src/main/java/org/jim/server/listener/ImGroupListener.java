package org.jim.server.listener;

import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
import org.jim.common.packets.Client;
import org.jim.common.packets.Command;
import org.jim.common.packets.ExitGroupNotifyRespBody;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
/**
 * @author WChao 
 * 2017年5月13日 下午10:38:36
 */
public class ImGroupListener implements GroupListener{

	/**
	 * 
	 * @author: WChao
	 */
	public ImGroupListener() {
	}

	/**
	 * @param args
	 * @author: WChao
	 */
	public static void main(String[] args) {

	}

	/** 
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterBind(ChannelContext channelContext, String group) throws Exception {
	}

	/** 
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
	}
}
