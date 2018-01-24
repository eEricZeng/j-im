/**
 * 
 */
package org.tio.im.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.packets.Client;
import org.tio.im.common.packets.User;
import org.tio.im.common.utils.ImKit;
import org.tio.utils.lock.SetWithLock;

import com.xiaoleilu.hutool.util.BeanUtil;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月22日 上午9:07:18
 */
public class ImAio {
	/**
	 * 功能描述：[根据用户ID获取当前用户]
	 * 创建者：WChao 创建时间: 2017年9月18日 下午4:34:39
	 * @param groupContext
	 * @param userid
	 * @return
	 */
	public static List<User> getUser(GroupContext groupContext,String userid){
		SetWithLock<ChannelContext> channelContexts = ImAio.getChannelContextsByUserid(groupContext, userid);
		if(channelContexts != null && !channelContexts.getObj().isEmpty()){
			List<User> users = new ArrayList<User>();
			for(ChannelContext channelContext : channelContexts.getObj()){
				ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
				Client client = imSessionContext.getClient();
				users.add(client.getUser());
			}
			return users;
		}
		return null;
	}
	/**
	 * 
		 * 功能描述：[根据用户ID获取当前用户所在通道集合]
		 * 创建者：WChao 创建时间: 2017年9月18日 下午4:34:39
		 * @param groupContext
		 * @param userid
		 * @return
		 *
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByUserid(GroupContext groupContext,String userid){
		SetWithLock<ChannelContext> channelContexts = Aio.getChannelContextsByUserid(groupContext, userid);
		return channelContexts;
	}
	/**
	 * 
		 * 功能描述：[获取所有用户(在线+离线)]
		 * 创建者：WChao 创建时间: 2017年9月18日 下午4:31:54
		 * @param groupContext
		 * @return
		 *
	 */
	public static List<User> getAllUser(GroupContext groupContext){
		List<User> users = new ArrayList<User>();
		Set<ChannelContext> userChannels = Aio.getAllChannelContexts(groupContext).getObj();
		if(userChannels == null)
			return users;
		for(ChannelContext channelContext : userChannels){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			Client client = imSessionContext.getClient();
			if(client != null && client.getUser() != null){
				User user = new User();
				BeanUtil.copyProperties(client.getUser(), user,"friends","groups");
				users.add(user);
			}
		}
		return users;
	}
	/**
	 * 
		 * 功能描述：[获取所有在线用户]
		 * 创建者：WChao 创建时间: 2017年9月18日 下午4:31:42
		 * @param groupContext
		 * @return
		 *
	 */
	public static List<User> getAllOnlineUser(GroupContext groupContext){
		List<User> users = new ArrayList<User>();
		Set<ChannelContext> userChannels = Aio.getAllConnectedsChannelContexts(groupContext).getObj();
		if(userChannels == null)
			return users;
		for(ChannelContext channelContext : userChannels){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			if(imSessionContext != null){
				Client client = imSessionContext.getClient();
				if(client != null && client.getUser() != null){
					User user = new User();
					BeanUtil.copyProperties(client.getUser(), user,"friends","groups");
					users.add(user);
				}
			}
		}
		return users;
	}
	
	/**
	 * 功能描述：[发送到群组(所有不同协议端)]
	 * 创建者：WChao 创建时间: 2017年9月21日 下午3:26:57
	 * @param groupContext
	 * @param group
	 * @param packet
	 */
	public static void sendToGroup(GroupContext groupContext, String group, ImPacket packet){
		if(packet.getBody() == null)
			return;
		SetWithLock<ChannelContext> withLockChannels = Aio.getChannelContextsByGroup(groupContext, group);
		if(withLockChannels == null)
			return;
		Set<ChannelContext> channels = withLockChannels.getObj();
		if(channels != null && channels.size() > 0){
			for(ChannelContext channelContext : channels){
				send(channelContext,packet);
			}
		}
	}
	/**
	 * 发送到指定通道(一个账号多通道);
	 * @param toChannleContexts
	 * @param packet
	 */
	public static void send(SetWithLock<ChannelContext> toChannleContexts,ImPacket packet){
		if(toChannleContexts == null || toChannleContexts.size() < 1)
			return;
		Set<ChannelContext> channels = toChannleContexts.getObj();
		if(channels.size() > 0){
			for(ChannelContext channelContext : channels){
				send(channelContext, packet);
			}
		}
	}
	/**
	 * 发送到指定通道;
	 * @param toChannleContexts
	 * @param packet
	 */
	public static void send(ChannelContext channelContext,ImPacket packet){
		if(channelContext == null)
			return;
		ImPacket rspPacket = ImKit.ConvertRespPacket(packet.getBody(), packet.getCommand(), channelContext);
		rspPacket.setSynSeq(packet.getSynSeq());
		Aio.sendToId(channelContext.getGroupContext(), channelContext.getId(), rspPacket);
	}
}
