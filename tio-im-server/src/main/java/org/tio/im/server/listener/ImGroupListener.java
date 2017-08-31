package org.tio.im.server.listener;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.ExitGroupNotifyRespBody;

import com.alibaba.fastjson.JSONObject;
/**
 * @author tanyaowu 
 * 2017年5月13日 下午10:38:36
 */
public class ImGroupListener implements GroupListener{

	/**
	 * 
	 * @author: tanyaowu
	 */
	public ImGroupListener() {
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	/** 
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterBind(ChannelContext channelContext, String group) throws Exception {
	}

	/** 
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		ExitGroupNotifyRespBody exitGroupNotifyRespBody = new ExitGroupNotifyRespBody();
		exitGroupNotifyRespBody.setGroup(group);
		exitGroupNotifyRespBody.setClient(imSessionContext.getClient());
		ImPacket respPacket2 = new ImPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, JSONObject.toJSONBytes(exitGroupNotifyRespBody));
		Aio.sendToGroup(channelContext.getGroupContext(), group, respPacket2);
	
	}
}
