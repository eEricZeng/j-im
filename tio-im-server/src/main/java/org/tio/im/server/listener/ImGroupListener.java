package org.tio.im.server.listener;

import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.ExitGroupNotifyRespBody;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.util.BeanUtil;
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
		//发退出房间通知  COMMAND_EXIT_GROUP_NOTIFY_RESP
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		ExitGroupNotifyRespBody exitGroupNotifyRespBody = new ExitGroupNotifyRespBody();
		exitGroupNotifyRespBody.setGroup(group);
		
		User notifyUser = new User();
		BeanUtil.copyProperties(imSessionContext.getClient().getUser(), notifyUser);
		notifyUser.setGroups(null);
		
		exitGroupNotifyRespBody.setUser(notifyUser);
		
		RespBody respBody = new RespBody(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP).setData(exitGroupNotifyRespBody.toString());
		ImPacket imPacket = new ImPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, JSONObject.toJSONBytes(respBody));
		ImAio.sendToGroup(channelContext.getGroupContext(), group, imPacket);
		
	}
}
