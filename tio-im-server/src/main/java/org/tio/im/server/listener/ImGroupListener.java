package org.tio.im.server.listener;

import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.Client;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.ExitGroupNotifyRespBody;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;
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
		//发退出房间通知  COMMAND_EXIT_GROUP_NOTIFY_RESP
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		ExitGroupNotifyRespBody exitGroupNotifyRespBody = new ExitGroupNotifyRespBody();
		exitGroupNotifyRespBody.setGroup(group);
		Client client = imSessionContext.getClient();
		if(client == null)
			return;
		User clientUser = client.getUser();
		if(clientUser == null)
			return;
		User notifyUser = new User(clientUser.getId(),clientUser.getNick());
		exitGroupNotifyRespBody.setUser(notifyUser);
		
		RespBody respBody = new RespBody(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP,exitGroupNotifyRespBody);
		ImPacket imPacket = new ImPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, respBody.toByte());
		ImAio.sendToGroup(group, imPacket);
		
	}
}
