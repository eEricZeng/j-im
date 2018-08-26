package org.jim.server.helper.redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImConfig;
import org.jim.common.cache.redis.JedisTemplate;
import org.jim.common.cache.redis.RedisCache;
import org.jim.common.cache.redis.RedisCacheManager;
import org.jim.common.listener.ImBindListener;
import org.jim.common.message.AbstractMessageHelper;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.Group;
import org.jim.common.packets.User;
import org.jim.common.packets.UserMessageData;
import org.jim.common.utils.ChatKit;
import org.jim.common.utils.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * Redis获取持久化+同步消息助手;
 * @author WChao
 * @date 2018年4月9日 下午4:39:30
 */
@SuppressWarnings("unchecked")
public class RedisMessageHelper extends AbstractMessageHelper{
	
	private RedisCache groupCache = null;
	private RedisCache pushCache = null;
	private RedisCache storeCache = null;
	private RedisCache userCache = null;
	
	private final String SUBFIX = ":";
	private Logger log = LoggerFactory.getLogger(RedisMessageHelper.class);
	
	static{
		RedisCacheManager.register(USER, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(GROUP, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(STORE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(PUSH, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public RedisMessageHelper(){
		this(null);
	}
	public RedisMessageHelper(ImConfig imConfig){
		this.groupCache = RedisCacheManager.getCache(GROUP);
		this.pushCache = RedisCacheManager.getCache(PUSH);
		this.storeCache = RedisCacheManager.getCache(STORE);
		this.userCache = RedisCacheManager.getCache(USER);
		this.imConfig = imConfig;
	}
	
	@Override
	public ImBindListener getBindListener() {
		
		return new RedisImBindListener(imConfig);
	}
	
	@Override
	public boolean isOnline(String userid) {
		try{
		Set<String> keys = JedisTemplate.me().keys(USER+SUBFIX+userid+SUBFIX+TERMINAL);
		if(keys != null && keys.size() > 0){
			Iterator<String> keyitr = keys.iterator();
			while(keyitr.hasNext()){
				String key = keyitr.next();
				key = key.substring(key.indexOf(userid));
				String isOnline = userCache.get(key, String.class);
				if(ONLINE.equals(isOnline)){
					return true;
				}
			}
		}
		}catch(Exception e){
			log.error(e.toString(),e);
		}
		return false;
	}
	
	@Override
	public List<String> getGroupUsers(String group_id) {
		String group_user_key = group_id+SUBFIX+USER;
		List<String> users = groupCache.listGetAll(group_user_key);
		return users;
	}

	
	@Override
	public void writeMessage(String timelineTable, String timelineId, ChatBody chatBody) {
		double score = chatBody.getCreateTime();
		RedisCacheManager.getCache(timelineTable).sortSetPush(timelineId, score, chatBody);
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
		List<String> messageList = pushCache.sortSetGetAll(key);
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
					List<String> messages = pushCache.sortSetGetAll(key);
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
		List<String> messages = pushCache.sortSetGetAll(key);
		if(messages == null || messages.size() == 0)
			return null;
		UserMessageData messageData = new UserMessageData(userid);
		putGroupMessage(messageData, JsonKit.toArray(messages, ChatBody.class));
		pushCache.remove(key);
		return messageData;
	}

	@Override
	public UserMessageData getFriendHistoryMessage(String userid, String from_userid,Double beginTime,Double endTime,Integer offset,Integer count) {
		String sessionId = ChatKit.sessionId(userid, from_userid);
		List<String> messages = null;
		String key = USER+SUBFIX+sessionId;
		boolean isTimeBetween = (beginTime != null && endTime != null);
		boolean isPage = (offset != null && count != null);
		if(isTimeBetween && !isPage){//消息区间，不分页
			messages = storeCache.sortSetGetAll(key, beginTime, endTime);
		}else if(isTimeBetween && isPage){//消息区间，并且分页;
			messages = storeCache.sortSetGetAll(key, beginTime, endTime,offset,count);
		}else if(!isTimeBetween &&  isPage){//所有消息，并且分页;
			messages = storeCache.sortSetGetAll(key, 0, Double.MAX_VALUE,offset,count);
		}else{//所有消息，不分页;
			messages = storeCache.sortSetGetAll(key);
		}
		if(messages == null || messages.size() == 0)
			return null;
		UserMessageData messageData = new UserMessageData(userid);
		putFriendsHistoryMessage(messageData, JsonKit.toArray(messages, ChatBody.class),from_userid);
		return messageData;
	}

	@Override
	public UserMessageData getGroupHistoryMessage(String userid, String groupid,Double beginTime,Double endTime,Integer offset,Integer count) {
		String key = GROUP+SUBFIX+groupid;
		List<String> messages = null;
		boolean isTimeBetween = (beginTime != null && endTime != null);
		boolean isPage = (offset != null && count != null);
		if(isTimeBetween && !isPage){//消息区间，不分页
			messages = storeCache.sortSetGetAll(key, beginTime, endTime);
		}else if(isTimeBetween && isPage){//消息区间，并且分页;
			messages = storeCache.sortSetGetAll(key, beginTime, endTime,offset,count);
		}else if(!isTimeBetween &&  isPage){//所有消息，并且分页;
			messages = storeCache.sortSetGetAll(key, 0, Double.MAX_VALUE,offset,count);
		}else{//所有消息，不分页;
			messages = storeCache.sortSetGetAll(key);
		}
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
	/**
	 * 获取群组所有成员信息
	 * @param group_id
	 * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
	 * @return
	 */
	@Override
	public Group getGroupUsers(String group_id, Integer type) {
		if(group_id == null || type == null)
			return null;
		Group group = groupCache.get(group_id+SUBFIX+INFO , Group.class);
		if(group == null)
			return null;
		List<String> userIds = this.getGroupUsers(group_id);
		if(userIds == null || userIds.isEmpty())
			return null;
		List<User> users = new ArrayList<User>();
		for(String userId : userIds){
			User user = getUserByType(userId, type);
			if(user != null){
				String status = user.getStatus();
				if(type == 0 && ONLINE.equals(status)){
					users.add(user);
				}else if(type == 1 && OFFLINE.equals(status)){
					users.add(user);
				}else if(type == 2){
					users.add(user);
				}
			}
		}
		group.setUsers(users);
		return group;
	}
	/**
	 * 根据在线类型获取用户信息;
	 * @param userid
	 * @param type
	 * @return
	 */
	@Override
	public User getUserByType(String userid,Integer type){
		User user = userCache.get(userid+SUBFIX+INFO, User.class);
		if(user == null)
			return null;
		boolean isOnline = this.isOnline(userid);
		String status = isOnline ? ONLINE : OFFLINE;
		if(type == 0 || type == 1){
			if(type == 0 && isOnline){
				user.setStatus(status);
			}else if(type == 1 && !isOnline){
				user.setStatus(status);
			}
		}else if(type == 2){
			user.setStatus(status);
		}
		return user;
	}
	/**
	 * 获取好友分组所有成员信息
	 * @param friend_group_id
	 * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
	 * @return
	 */
	
	@Override
	public Group getFriendUsers(String user_id , String friend_group_id, Integer type) {
		if(user_id == null || friend_group_id == null || type == null)
			return null;
		List<Group> friends = userCache.get(user_id+SUBFIX+FRIENDS, List.class);
		if(friends == null || friends.isEmpty())
			return null;
		for(Group group : friends){
			if(friend_group_id.equals(group.getGroup_id())){
				List<User> users = group.getUsers();
				if(users == null || users.isEmpty())
					return null;
				List<User> userResults = new ArrayList<User>();
				for(User user : users){
					initUserStatus(user);
					String status = user.getStatus();
					if(type == 0 && ONLINE.equals(status)){
						userResults.add(user);
					}else if(type == 1 && OFFLINE.equals(status)){
						userResults.add(user);
					}else{
						userResults.add(user);
					}
				}
				group.setUsers(userResults);
				return group;
			}
		}
		return null;
	}
	/**
	 * 初始化用户在线状态;
	 * @param user
	 */
	public void initUserStatus(User user){
		if(user == null)
			return ;
		String userid = user.getId();
		boolean isOnline = this.isOnline(userid);
		if(isOnline){
			user.setStatus(ONLINE);
		}else{
			user.setStatus(OFFLINE);
		}
	}
	/**
	 * 获取好友分组所有成员信息
	 * @param user_id
	 * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
	 * @return
	 */
	@Override
	public List<Group> getAllFriendUsers(String user_id,Integer type) {
		if(user_id == null)
			return null;
		List<JSONObject> friendJsonArray = userCache.get(user_id+SUBFIX+FRIENDS, List.class);
		if(friendJsonArray == null || friendJsonArray.isEmpty())
			return null;
		List<Group> friends = new ArrayList<Group>();
		for(JSONObject groupJson : friendJsonArray){
			Group group = JSONObject.toJavaObject(groupJson, Group.class);
			List<User> users = group.getUsers();
			if(users == null || users.isEmpty())
				continue;
			List<User> userResults = new ArrayList<User>();
			for(User user : users){
				initUserStatus(user);
				String status = user.getStatus();
				if(type == 0 && ONLINE.equals(status)){
					userResults.add(user);
				}else if(type == 1 && OFFLINE.equals(status)){
					userResults.add(user);
				}else if(type == 2){
					userResults.add(user);
				}
			}
			group.setUsers(userResults);
			friends.add(group);
		}
		return friends;
	}
	/**
	 * 获取群组所有成员信息（在线+离线)
	 * @param user_id
	 * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
	 * @return
	 */
	@Override
	public List<Group> getAllGroupUsers(String user_id,Integer type) {
		if(user_id == null)
			return null;
		List<String> group_ids = userCache.listGetAll(user_id+SUBFIX+GROUP);
		if(group_ids == null || group_ids.isEmpty())
			return null;
		List<Group> groups = new ArrayList<Group>();
		for(String group_id : group_ids){
			Group group = getGroupUsers(group_id, type);
			if(group != null){
				groups.add(group);
			}
		}
		return groups;
	}
	/**
	 * 获取用户拥有的群组;
	 * @param user_id
	 * @return
	 */
	@Override
	public List<String> getGroups(String user_id) {
		List<String> groups = userCache.listGetAll(user_id+SUBFIX+GROUP);
		return groups;
	}
}
