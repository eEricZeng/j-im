package org.tio.im.server.helper.redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.Const;
import org.tio.im.common.cache.redis.JedisTemplate;
import org.tio.im.common.cache.redis.RedisCache;
import org.tio.im.common.cache.redis.RedisCacheManager;
import org.tio.im.common.listener.ImBindListener;
import org.tio.im.common.message.IMesssageHelper;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.UserMessageData;
import org.tio.im.common.utils.ChatKit;
import org.tio.im.common.utils.JsonKit;

/**
 * Redis获取持久化+同步消息助手;
 * @author WChao
 * @date 2018年4月9日 下午4:39:30
 */
public class RedisMessageHelper implements IMesssageHelper,Const {
	
	private RedisCache groupCache = null;
	private RedisCache pushCache = null;
	private RedisCache storeCache = null;
	private RedisCache userCache = null;
	
	private final String SUBFIX = ":";
	private Logger log = LoggerFactory.getLogger(RedisMessageHelper.class);
	
	public RedisMessageHelper(){
		this.groupCache = RedisCacheManager.getCache(GROUP);
		this.pushCache = RedisCacheManager.getCache(PUSH);
		this.storeCache = RedisCacheManager.getCache(STORE);
		this.userCache = RedisCacheManager.getCache(USER);
	}
	static{
		RedisCacheManager.register(USER, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(GROUP, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(STORE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(PUSH, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	@Override
	public ImBindListener getBindListener() {
		
		return new RedisImBindListener();
	}
	
	@Override
	public List<String> getGroupUsers(String group_id) {
		List<String> users = groupCache.listGetAll(group_id);
		return users;
	}

	
	@Override
	public void writeMessage(String timelineTable, String timelineId, ChatBody chatBody) {
		RedisCacheManager.getCache(timelineTable).listPushTail(timelineId, chatBody);
	}


	@Override
	public void addGroupUser(String userid, String group_id) {
		List<String> users = groupCache.listGetAll(group_id);
		if(!users.contains(userid)){
			groupCache.listPushTail(group_id, userid);
		}
	}

	@Override
	public void removeGroupUser(String userid, String group_id) {
		groupCache.listRemove(group_id,userid);
	}

	@Override
	public UserMessageData getFriendsOfflineMessage(String userid, String from_userid) {
		String key = USER+SUBFIX+userid+SUBFIX+from_userid;
		List<String> messageList = pushCache.listGetAll(key);
		List<ChatBody> datas = JsonKit.toArray(messageList, ChatBody.class);
		pushCache.remove(key);
		return putFriendsMessage(new UserMessageData(userid), datas);
	}

	@Override
	public UserMessageData getFriendsOfflineMessage(String userid) {
		try{
			Set<String> keys = JedisTemplate.me().keys(PUSH+SUBFIX+USER+SUBFIX+userid);
			UserMessageData messageData = new UserMessageData(userid);
			if(keys != null && keys.size() > 0){
				List<ChatBody> results = new ArrayList<ChatBody>();
				Iterator<String> keyitr = keys.iterator();
				while(keyitr.hasNext()){//获取好友离线消息;
					String key = keyitr.next();
					key = key.substring(key.indexOf(USER+SUBFIX));
					List<String> messages = pushCache.listGetAll(key);
					pushCache.remove(key);
					results.addAll(JsonKit.toArray(messages, ChatBody.class));
				}
				putFriendsMessage(messageData, results);
			}
			List<String> groups = userCache.listGetAll(userid+SUBFIX+GROUP);
			if(groups != null){//获取群组离线消息;
				for(String groupid : groups){
					UserMessageData groupMessageData = getGroupOfflineMessage(userid, groupid);
					if(groupMessageData != null){
						putGroupMessage(messageData, groupMessageData.getGroups().get(groupid));
					}
				}
			}
			return messageData;
		}catch (Exception e) {
			log.error(e.toString(),e);
		}
		return null;
	}

	@Override
	public UserMessageData getGroupOfflineMessage(String userid, String groupid) {
		String key = GROUP+SUBFIX+groupid+SUBFIX+userid;
		List<String> messages = pushCache.listGetAll(key);
		if(messages == null || messages.size() == 0)
			return null;
		UserMessageData messageData = new UserMessageData(userid);
		putGroupMessage(messageData, JsonKit.toArray(messages, ChatBody.class));
		pushCache.remove(key);
		return messageData;
	}

	@Override
	public UserMessageData getFriendHistoryMessage(String userid, String from_userid) {
		String sessionId = ChatKit.sessionId(userid, from_userid);
		List<String> messages = storeCache.listGetAll(USER+SUBFIX+sessionId);
		if(messages == null || messages.size() == 0)
			return null;
		UserMessageData messageData = new UserMessageData(userid);
		putFriendsHistoryMessage(messageData, JsonKit.toArray(messages, ChatBody.class),from_userid);
		return messageData;
	}

	@Override
	public UserMessageData getGroupHistoryMessage(String userid, String groupid) {
		List<String> messages = storeCache.listGetAll(GROUP+SUBFIX+groupid);
		if(messages == null || messages.size() == 0)
			return null;
		UserMessageData messageData = new UserMessageData(userid);
		putGroupMessage(messageData, JsonKit.toArray(messages, ChatBody.class));
		return messageData;
	}
	
	/**
	 * 放入用户群组消息;
	 * @param userMessage
	 * @param messages
	 */
	public UserMessageData putGroupMessage(UserMessageData userMessage,List<ChatBody> messages){
		if(userMessage == null || messages == null)
			return null;
		for(ChatBody chatBody : messages){
			String group = chatBody.getGroup_id();
			if(StringUtils.isEmpty(group))
				continue;
			List<ChatBody> groupMessages = userMessage.getGroups().get(group);
			if(groupMessages == null){
				groupMessages = new ArrayList<ChatBody>();
				userMessage.getGroups().put(group, groupMessages);
			}
			groupMessages.add(chatBody);
		}
		return userMessage;
	}
	/**
	 * 放入用户好友消息;
	 * @param userMessage
	 * @param messages
	 */
	public UserMessageData putFriendsMessage(UserMessageData userMessage , List<ChatBody> messages){
		if(userMessage == null || messages == null)
			return null;
		for(ChatBody chatBody : messages){
			String fromUserId = chatBody.getFrom();
			if(StringUtils.isEmpty(fromUserId))
				continue;
			List<ChatBody> friendMessages = userMessage.getFriends().get(fromUserId);
			if(friendMessages == null){
				friendMessages = new ArrayList<ChatBody>();
				userMessage.getFriends().put(fromUserId, friendMessages);
			}
			friendMessages.add(chatBody);
		}
		return userMessage;
	}
	/**
	 * 放入用户好友历史消息;
	 * @param userMessage
	 * @param messages
	 */
	public UserMessageData putFriendsHistoryMessage(UserMessageData userMessage , List<ChatBody> messages,String friendId){
		if(userMessage == null || messages == null)
			return null;
		for(ChatBody chatBody : messages){
			String fromUserId = chatBody.getFrom();
			if(StringUtils.isEmpty(fromUserId))
				continue;
			List<ChatBody> friendMessages = userMessage.getFriends().get(friendId);
			if(friendMessages == null){
				friendMessages = new ArrayList<ChatBody>();
				userMessage.getFriends().put(friendId, friendMessages);
			}
			friendMessages.add(chatBody);
		}
		return userMessage;
	}
}
