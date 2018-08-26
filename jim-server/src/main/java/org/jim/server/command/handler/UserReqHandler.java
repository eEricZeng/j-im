/**
 * 
 */
package org.jim.server.command.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.Const;
import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.message.IMesssageHelper;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.packets.UserReqBody;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbCmdHandler;
import org.tio.core.ChannelContext;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月18日 下午4:08:47
 */
public class UserReqHandler extends AbCmdHandler{

	@Override
	public Command command() {
		return Command.COMMAND_GET_USER_REQ;
	}

	@Override
	public ImPacket handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		UserReqBody userReqBody = JsonKit.toBean(packet.getBody(),UserReqBody.class);
		User user = null;
		RespBody resPacket = null;
		
		String userid = userReqBody.getUserid();
		if(StringUtils.isEmpty(userid))
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10004), channelContext);
		
		Integer type = userReqBody.getType() == null ? 2 : userReqBody.getType();//(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线]);
		if(0 == userReqBody.getType() || 1 == userReqBody.getType() || 2 == userReqBody.getType()){
			user = getUserInfo(userid, type);
			if(0 == userReqBody.getType()){//在线用户
				resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10005);
			}else if(1 == userReqBody.getType()){//离线用户;
				resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10006);
			}else if(2 == userReqBody.getType()){//在线+离线用户;
				resPacket = new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10003);
			}
		}
		if(user == null)
			return ImKit.ConvertRespPacket(new RespBody(Command.COMMAND_GET_USER_RESP,ImStatus.C10004), channelContext);
		resPacket.setData(user);
		return ImKit.ConvertRespPacket(resPacket, channelContext);
	}
	
	  /**
     * 根据用户id获取用户在线及离线用户;
     * @param userid
     * @param type(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线])
     * @return
     */
    public User getUserInfo(String userid , Integer type){
    	User user = null;
    	boolean isStore = Const.ON.equals(imConfig.getIsStore());//是否开启持久化;
    	IMesssageHelper messageHelper = imConfig.getMessageHelper();//消息持久化助手;
    	if(isStore){
    		user = messageHelper.getUserByType(userid, 2);
    		if(user == null)
    			return null;
			user.setFriends(messageHelper.getAllFriendUsers(userid, type));
			user.setGroups(messageHelper.getAllGroupUsers(userid, type));
			return user;
		}else{
			user = ImAio.getUser(userid);
		   	if(user == null)
		   		return null;
	   		 User copyUser =ImKit.copyUserWithoutFriendsGroups(user);
	   		 if(type == 0 || type == 1){//在线用户;
	   			//处理好友分组在线用户相关信息;
	   			 List<Group> friends = user.getFriends();
	   			 List<Group> onlineFriends = initOnlineUserFriendsGroups(friends,type,0);
	   			 if(onlineFriends != null){
	   				 copyUser.setFriends(onlineFriends);
	   			 }
	   			 //处理群组在线用户相关信息;
	   			 List<Group> groups = user.getGroups();
	   			 List<Group> onlineGroups = initOnlineUserFriendsGroups(groups,type,1);
	   			 if(onlineGroups != null){
	   				 copyUser.setGroups(onlineGroups);
	   			 }
	   			 return copyUser;
	   		 }else if(type == 2){//所有用户(在线+离线);
	   			 return user;
	   		 }
		}
	   return user;
    }
    /**
     * 处理在线用户好友及群组用户;
     * @param groups
     * @param user
     * @param flag(0：好友,1:群组)
     * @return
     */
    private static List<Group> initOnlineUserFriendsGroups(List<Group> groups,Integer type,Integer flag){
	   	 if(groups == null || groups.isEmpty())
	   		 return null;
	   	 //处理好友分组在线用户相关信息;
		 List<Group> onlineGroups = new ArrayList<Group>();
		 for(Group group : groups){
			 Group copyGroup = ImKit.copyGroupWithoutUsers(group);
			 List<User> users = null;
			 if(flag == 1){
				 users = ImAio.getAllUserByGroup(group.getGroup_id());
			 }else if(flag == 0){
				 users = group.getUsers();
			 }
			 if(users != null && !users.isEmpty()){
				 List<User> copyUsers = new ArrayList<User>();
				 for(User userObj : users){
					 User onlineUser = ImAio.getUser(userObj.getId());
					 if(onlineUser != null && type == 0){//在线
						 User copyOnlineUser = ImKit.copyUserWithoutFriendsGroups(onlineUser);
						 copyUsers.add(copyOnlineUser);
					 }else if(onlineUser == null && type == 1){//离线
						 User copyOnlineUser = ImKit.copyUserWithoutFriendsGroups(onlineUser);
						 copyUsers.add(copyOnlineUser);
					 }
				 }
				 copyGroup.setUsers(copyUsers);
			 }
			 onlineGroups.add(copyGroup);
		 }
		 return onlineGroups;
    }
}
