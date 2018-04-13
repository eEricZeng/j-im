package org.jim.server.helper.redis;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.Const;
import org.jim.common.ImConfig;
import org.tio.core.ChannelContext;
import org.jim.common.cache.redis.RedisCache;
import org.jim.common.cache.redis.RedisCacheManager;
import org.jim.common.listener.ImBindListener;
/**
 * @author WChao
 * @date 2018年4月8日 下午4:12:31
 */
public class RedisImBindListener implements ImBindListener,Const{
	
	private RedisCache groupCache = null;
	private RedisCache userCache = null;
	private final String SUBFIX = ":";
	
	public RedisImBindListener(){
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
		String userid = channelContext.getUserid();
		initGroupUsers(group,userid);
	}

	@Override
	public void onAfterGroupUnbind(ChannelContext channelContext, String group) throws Exception {
		if(!isStore())
			return;
		String userid = channelContext.getUserid();
		groupCache.listRemove(group, userid);//移除群组成员;
		RedisCacheManager.getCache(PUSH).remove(GROUP+SUBFIX+group+SUBFIX+userid);
	}

	@Override
	public void onAfterUserBind(ChannelContext channelContext, String userid) throws Exception {
		if(!isStore())
			return;
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
	public void initGroupUsers(String group ,String userid){
		if(!isStore())
			return;
		if(StringUtils.isEmpty(group) || StringUtils.isEmpty(userid))
			return;
		List<String> users = groupCache.listGetAll(group);
		if(!users.contains(userid)){
			groupCache.listPushTail(group, userid);
		}
		initUserGroups(userid, group);
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
	//是否开启持久化;
	public boolean isStore(){
		return ON.equals(ImConfig.isStore);
	}
}
