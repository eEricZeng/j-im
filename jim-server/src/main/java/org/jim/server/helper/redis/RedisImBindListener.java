package org.jim.server.helper.redis;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImConfig;
import org.jim.common.ImSessionContext;
import org.jim.common.cache.redis.RedisCache;
import org.jim.common.cache.redis.RedisCacheManager;
import org.jim.common.listener.AbstractImBindListener;
import org.jim.common.packets.Client;
import org.jim.common.packets.Group;
import org.jim.common.packets.User;
import org.jim.common.utils.ImKit;
import org.tio.core.ChannelContext;
/**
 * @author WChao
 * @date 2018年4月8日 下午4:12:31
 */
public class RedisImBindListener extends AbstractImBindListener{
	
	private RedisCache groupCache = null;
	private RedisCache userCache = null;
	private final String SUBFIX = ":";
	
	public RedisImBindListener(){
		this(null);
	}
	
	public RedisImBindListener(ImConfig imConfig){
		this.imConfig = imConfig;
		groupCache = RedisCacheManager.getCache(GROUP);
		userCache = RedisCacheManager.getCache(USER);
	}
	
	static{
		RedisCacheManager.register(USER, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(GROUP, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(STORE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(PUSH, Integer.MAX_VALUE, Integer.MAX_VALUE);
		
	}
	
	@Override
	public void onAfterGroupBind(ChannelContext channelContext, String group) throws Exception {
		if(!isStore())
			return;
		initGroupUsers(group,channelContext);
	}

	@Override
	public void onAfterGroupUnbind(ChannelContext channelContext, String group) throws Exception {
		if(!isStore())
			return;
		String userid = channelContext.getUserid();
		groupCache.listRemove(group+SUBFIX+USER, userid);//移除群组成员;
		userCache.listRemove(userid+SUBFIX+GROUP, group);//移除成员群组;
		RedisCacheManager.getCache(PUSH).remove(GROUP+SUBFIX+group+SUBFIX+userid);
	}

	@Override
	public void onAfterUserBind(ChannelContext channelContext, String userid) throws Exception {
		if(!isStore())
			return;
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		Client client = imSessionContext.getClient();
		if(client == null)
			return;
		User onlineUser = client.getUser();
		if(onlineUser != null){
			initUserTerminal(channelContext,onlineUser.getTerminal(),ONLINE);
			initUserInfo(onlineUser);
		}
	}

	@Override
	public void onAfterUserUnbind(ChannelContext channelContext, String userid) throws Exception {
		if(!isStore())
			return;
		
	}
	/**
	 * 初始化群组用户;
	 * @param group
	 * @param userid
	 */
	public void initGroupUsers(String groupid ,ChannelContext channelContext){
		if(!isStore())
			return;
		String userid = channelContext.getUserid();
		if(StringUtils.isEmpty(groupid) || StringUtils.isEmpty(userid))
			return;
		String group_user_key = groupid+SUBFIX+USER;
		List<String> users = groupCache.listGetAll(group_user_key);
		if(!users.contains(userid)){
			groupCache.listPushTail(group_user_key, userid);
		}
		initUserGroups(userid, groupid);
		
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		Client client = imSessionContext.getClient();
		if(client == null)
			return;
		User onlineUser = client.getUser();
		if(onlineUser == null)
			return;
		List<Group> groups = onlineUser.getGroups();
		if(groups == null)
			return;
		for(Group group : groups){
			if(groupid.equals(group.getGroup_id())){
				groupCache.put(groupid+SUBFIX+INFO, group);
				break;
			}
		}
	}
	/**
	 * 初始化用户拥有哪些群组;
	 * @param userid
	 * @param group
	 */
	public void initUserGroups(String userid, String group){
		if(!isStore())
			return;
		if(StringUtils.isEmpty(group) || StringUtils.isEmpty(userid))
			return;
		List<String> groups = userCache.listGetAll(userid+SUBFIX+GROUP);
		if(!groups.contains(group)){
			userCache.listPushTail(userid+SUBFIX+GROUP, group);
		}
	}
	/**
	 * 初始化用户终端协议类型;
	 * @param userid
	 * @param status(online、offline)
	 */
	public void initUserTerminal(ChannelContext channelContext , String terminal , String status){
		if(!isStore())
			return;
		String userid = channelContext.getUserid();
		if(StringUtils.isEmpty(userid) || StringUtils.isEmpty(terminal))
			return;
		userCache.put(userid+SUBFIX+TERMINAL+SUBFIX+terminal, status);
	}
	/**
	 * 初始化用户终端协议类型;
	 * @param userid
	 * @param status(online、offline)
	 */
	public void initUserInfo(User user){
		if(!isStore() || user == null)
			return;
		String userid = user.getId();
		if(StringUtils.isEmpty(userid))
			return;
		User userCopy = ImKit.copyUserWithoutFriendsGroups(user);
		userCache.put(userid+SUBFIX+INFO, userCopy);
		List<Group> friends = user.getFriends();
		if(friends != null){
			userCache.put(userid+SUBFIX+FRIENDS, (Serializable) friends);
		}
	}
	//是否开启持久化;
	public boolean isStore(){
		return ON.equals(imConfig.getIsStore());
	}
}
