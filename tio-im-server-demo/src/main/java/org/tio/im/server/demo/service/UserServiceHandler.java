/**
 * 
 */
package org.tio.im.server.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.im.common.Const;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.packets.Group;
import org.tio.im.common.packets.LoginReqBody;
import org.tio.im.common.packets.User;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.im.common.utils.Md5;
import org.tio.im.server.command.handler.proc.login.LoginReqCmdIntf;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * @author WChao
 *
 */
public class UserServiceHandler implements LoginReqCmdIntf{

	@Override
	public boolean isProtocol(ChannelContext channelContext) {
		 
		return true;
	}

	@Override
	public String name() {
		
		return "default";
	}

	@Override
	public User getUser(LoginReqBody loginReqBody , ChannelContext channelContext) {
		String loginname = loginReqBody.getLoginname();
		String password = loginReqBody.getPassword();
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		String handshakeToken = imSessionContext.getToken();
		if (!StringUtils.isBlank(handshakeToken)) {
			return this.getUser(handshakeToken);
		}
		return this.getUser(loginname, password);
	}
	public static final Map<String, User> tokenMap = new HashMap<>();

	private static String[] familyName = new String[] { "谭", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
			"吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱" };

	private static String[] secondName = new String[] { "艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
			"明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
			"大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
			"子家", "德利优视", "五方会谈", "来电话了", "T-IO", "Talent" ,"轨迹","超"};
	
	/**
	 * 根据用户名和密码获取用户
	 * @param loginname
	 * @param password
	 * @return
	 * @author: WChao
	 */
	public User getUser(String loginname, String password) {
		String text = loginname+password;
		String key = Const.authkey;
		String token = Md5.sign(text, key, HttpConst.CHARSET_NAME);
		User user = getUser(token);
		user.setId(loginname);
		return user;
	}
	/**
	 * 根据token获取用户信息
	 * @param token
	 * @return
	 * @author: WChao
	 */
	public User getUser(String token) {
		//demo中用map，生产环境需要用cache
		User user = tokenMap.get(token);
		if (user == null) {
			user = new User();
			user.setId(UUIDSessionIdGenerator.instance.sessionId(null));
			user.setNick(familyName[RandomUtil.randomInt(0, familyName.length - 1)] + secondName[RandomUtil.randomInt(0, secondName.length - 1)]);
			
			user.setGroups(initGroups(user));
			user.setFriends(initFriends(user));
			user.setAvatar(nextImg());
			
			if (tokenMap.size() > 10000) {
				tokenMap.clear();
			}
			tokenMap.put(token, user);
		}
		return user;
	}
	
	public List<Group> initGroups(User user){
		//模拟的群组;正式根据业务去查数据库或者缓存;
		List<Group> groups = new ArrayList<Group>();
		groups.add(new Group("100","tio-im朋友圈"));
		return groups;
	}
	public List<Group> initFriends(User user){
		List<Group> friends = new ArrayList<Group>();
		Group myFriend = new Group("1","我的好友");
		List<User> myFriendGroupUsers = new ArrayList<User>();
		User user1 = new User();
		user1.setId(UUIDSessionIdGenerator.instance.sessionId(null));
		user1.setNick(familyName[RandomUtil.randomInt(0, familyName.length - 1)] + secondName[RandomUtil.randomInt(0, secondName.length - 1)]);
		user1.setAvatar(nextImg());
		myFriendGroupUsers.add(user1);
		myFriend.setUsers(myFriendGroupUsers);
		friends.add(myFriend);
		return friends;
	}
	
	public String nextImg() {
		return ImgMnService.nextImg();
	}

	public String newToken() {
		return UUID.randomUUID().toString();
	}
}
