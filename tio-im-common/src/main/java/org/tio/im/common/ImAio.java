/**
 * 
 */
package org.tio.im.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.listener.ImBindListener;
import org.tio.im.common.packets.Client;
import org.tio.im.common.packets.User;
import org.tio.im.common.utils.ImKit;
import org.tio.utils.lock.SetWithLock;

import cn.hutool.core.bean.BeanUtil;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月22日 上午9:07:18
 */
public class ImAio {
	
	private static GroupContext groupContext = ImConfig.groupContext;
	
	private static Logger log = LoggerFactory.getLogger(ImAio.class);
	/**
	 * 功能描述：[根据用户ID获取当前用户]
	 * 创建者：WChao 创建时间: 2017年9月18日 下午4:34:39
	 * @param groupContext
	 * @param userid
	 * @return
	 */
	public static List<User> getUser(String userid){
		SetWithLock<ChannelContext> userChannelContexts = ImAio.getChannelContextsByUserid(userid);
		List<User> users = new ArrayList<User>();
		if(userChannelContexts == null)
			return users;
		ReadLock readLock = userChannelContexts.getLock().readLock();
		readLock.lock();
		try{
			Set<ChannelContext> userChannels = userChannelContexts.getObj();
			if(userChannels == null )
				return users;
			for(ChannelContext channelContext : userChannels){
				ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
				Client client = imSessionContext.getClient();
				users.add(client.getUser());
			}
			return users;
		}finally {
			readLock.unlock();
		}
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
	public static SetWithLock<ChannelContext> getChannelContextsByUserid(String userid){
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
	public static List<User> getAllUser(){
		List<User> users = new ArrayList<User>();
		SetWithLock<ChannelContext> allChannels = Aio.getAllChannelContexts(groupContext);
		if(allChannels == null)
			return users;
		ReadLock readLock = allChannels.getLock().readLock();
		readLock.lock();
		try{
			Set<ChannelContext> userChannels = allChannels.getObj();
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
		}finally {
			readLock.unlock();
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
	public static List<User> getAllOnlineUser(){
		List<User> users = new ArrayList<User>();
		SetWithLock<ChannelContext> onlineChannels = Aio.getAllConnectedsChannelContexts(groupContext);
		if(onlineChannels == null)
			return users;
		ReadLock readLock = onlineChannels.getLock().readLock();
		readLock.lock();
		try{
			Set<ChannelContext> userChannels = onlineChannels.getObj();
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
		}finally {
			readLock.unlock();
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
	public static void sendToGroup(String group, ImPacket packet){
		if(packet.getBody() == null)
			return;
		SetWithLock<ChannelContext> withLockChannels = Aio.getChannelContextsByGroup(groupContext, group);
		if(withLockChannels == null)
			return;
		ReadLock readLock = withLockChannels.getLock().readLock();
		readLock.lock();
		try{
			Set<ChannelContext> channels = withLockChannels.getObj();
			if(channels != null && channels.size() > 0){
				for(ChannelContext channelContext : channels){
					send(channelContext,packet);
				}
			}
		}finally{
			readLock.unlock();
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
	/**
	 * 发送到指定用户;
	 * @param toChannleContexts
	 * @param packet
	 */
	public static void sendToUser(String userid,ImPacket packet){
		if(StringUtils.isEmpty(userid))
			return;
		SetWithLock<ChannelContext> toChannleContexts = getChannelContextsByUserid(userid);
		if(toChannleContexts == null || toChannleContexts.size() < 1)
			return;
		ReadLock readLock = toChannleContexts.getLock().readLock();
		readLock.lock();
		try{
			Set<ChannelContext> channels = toChannleContexts.getObj();
			for(ChannelContext channelContext : channels){
				send(channelContext, packet);
			}
		}finally{
			readLock.unlock();
		}
	}
	/**
	 * 绑定用户;
	 * @param channelContext
	 * @param userid
	 */
	public static void bindUser(ChannelContext channelContext,String userid){
		bindUser(channelContext, userid,null);
	}
	/**
	 * 绑定用户,同时可传递监听器执行回调函数
	 * @param channelContext
	 * @param userid
	 * @param bindListener(绑定监听器回调)
	 */
	public static void bindUser(ChannelContext channelContext,String userid,ImBindListener bindListener){
		Aio.bindUser(channelContext, userid);
		if(bindListener != null){
			try {
				bindListener.onAfterUserBind(channelContext, userid);
			} catch (Exception e) {
				log.error(e.toString(),e);
			}
		}
	}
	/**
	 * 解绑用户
	 * @param groupContext
	 * @param userid
	 */
	public static void unbindUser(String userid){
		unbindUser(userid, null);
	}
	/**
	 * 解除绑定用户,同时可传递监听器执行回调函数
	 * @param channelContext
	 * @param userid
	 * @param bindListener(解绑定监听器回调)
	 */
	public static void unbindUser(String userid,ImBindListener bindListener){
		Aio.unbindUser(groupContext, userid);
		if(bindListener != null){
			try {
				SetWithLock<ChannelContext> userChannelContexts = ImAio.getChannelContextsByUserid(userid);
				if(userChannelContexts == null || userChannelContexts.size() == 0)
					return ;
				ReadLock readLock = userChannelContexts.getLock().readLock();
				readLock.lock();
				try{
					Set<ChannelContext> channels = userChannelContexts.getObj();
					for(ChannelContext channelContext : channels){
						bindListener.onAfterUserBind(channelContext, userid);
					}
				}finally{
					readLock.unlock();
				}
			} catch (Exception e) {
				log.error(e.toString(),e);
			}
		}
	}
	/**
	 * 绑定群组;
	 * @param channelContext
	 * @param group
	 */
	public static void bindGroup(ChannelContext channelContext,String group){
		bindGroup(channelContext, group,null);
	}
	/**
	 * 绑定群组,同时可传递监听器执行回调函数
	 * @param channelContext
	 * @param group
	 * @param binListener(绑定监听器回调)
	 */
	public static void bindGroup(ChannelContext channelContext,String group,ImBindListener bindListener){
		Aio.bindGroup(channelContext, group);
		if(bindListener != null){
			try {
				bindListener.onAfterGroupBind(channelContext, group);
			} catch (Exception e) {
				log.error(e.toString(),e);
			}
		}
	}
	/**
	 * 与指定群组解除绑定
	 * @param userid
	 * @param group
	 * @param bindListener
	 */
	public static void unbindGroup(String userid,String group){
		unbindGroup(userid, group, null);
	}
	/**
	 * 与指定群组解除绑定,同时可传递监听器执行回调函数
	 * @param channelContext
	 * @param group
	 * @param binListener(解绑定监听器回调)
	 */
	public static void unbindGroup(String userid,String group,ImBindListener bindListener){
		SetWithLock<ChannelContext> userChannelContexts = ImAio.getChannelContextsByUserid(userid);
		if(userChannelContexts == null || userChannelContexts.size() == 0)
			return ;
		ReadLock readLock = userChannelContexts.getLock().readLock();
		readLock.lock();
		try{
			Set<ChannelContext> channels = userChannelContexts.getObj();
			for(ChannelContext channelContext : channels){
				Aio.unbindGroup(group, channelContext);
				if(bindListener != null){
					try {
						bindListener.onAfterGroupUnbind(channelContext, group);
					} catch (Exception e) {
						log.error(e.toString(),e);
					}
				}
			}
		}finally{
			readLock.unlock();
		}
	}
	/**
	 * 移除用户, 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param userid
	 * @param remark
	 */
	public static void remove(String userid,String remark){
		SetWithLock<ChannelContext> userChannelContexts = getChannelContextsByUserid(userid);
		if(userChannelContexts.size() > 0){
			ReadLock readLock = userChannelContexts.getLock().readLock();
			readLock.lock();
			try{
				Set<ChannelContext> channels = userChannelContexts.getObj();
				for(ChannelContext channelContext : channels){
					remove(channelContext, remark);
				}
			}finally{
				readLock.unlock();
			}
		}
	}
	/**
	 * 移除指定channel, 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param userid
	 * @param remark
	 */
	public static void remove(ChannelContext channelContext,String remark){
		Aio.remove(channelContext, remark);
	}
}
