package org.tio.im.server.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.tio.im.common.Const;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.packets.User;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.im.common.utils.Md5;

import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * 这个类是mock类，用户实际项目中需要用自己的代码替换现有的代码
 * @author tanyaowu 
 * 2017年5月8日 下午6:12:47
 */
public class UserService {

	public static final Map<String, User> tokenMap = new HashMap<>();
	//模拟头像
	//	private static String[] avatars = new String[] { "http://wx.qlogo.cn/mmopen/O8EdbgD8IxibvOhZ6ib912pLkeAfHfmWRkvr4POl2OF98JDlrSQxtmsCUO9ZqicVZJUjOWicTIEOrvjqZFjhfZ9Ma5c3kF855Yde/0", 
	//			"http://wx.qlogo.cn/mmopen/FiaIW3HsXzg7cYbAlYlibdicbw2xwj1QucyIoWQZ2HzvVXDh5AwwewXwsOO7891Rx5OH8PQS2DjGL1EXw3nFajRYw/0",
	//			"http://wx.qlogo.cn/mmopen/Q3auHgzwzM5PzRt6X82qk7cp1n5OxLUBBD1JXCXyVE7rbPKl8KrMIteGfBqZKYAdGxVScu4HCccP9HMyJ4fp1Q/0", 
	//			"http://wx.qlogo.cn/mmopen/ajNVdqHZLLCNwrWvNcCNoKadyGbJibEDiak2aqftjrtNvuIFmibiciaicpRDwD5kkkdILDOzWHr8tlKFISuLs0Y9HGtA/0",
	//			"http://wx.qlogo.cn/mmopen/snImia2EJEZ90wAn06WPypdUqrGbFMNW9WTNKSsa55gN1XAic7XPHtZsBtTGP4DnAQoPwIgRaBn6NnpsRLBt4aq7pdVymqHtY3/0", 
	//			"http://wx.qlogo.cn/mmopen/PiajxSqBRaEI3icyOv9fpULldSNibfgDsYJqAZM1sTs5ibJzCiaxDptiahLCEnCicibOYbZcmpgjMdqTRib34uyKDEFDNNw/0" };

	private static String[] familyName = new String[] { "谭", "刘", "张", "李", "胡", "沈", "朱", "钱", "王", "伍", "赵", "孙", "吕", "马", "秦", "毛", "成", "梅", "黄", "郭", "杨", "季", "童", "习", "郑",
			"吴", "周", "蒋", "卫", "尤", "何", "魏", "章", "郎", " 唐", "汤", "苗", "孔", "鲁", "韦", "任", "袁", "贺", "狄朱" };

	private static String[] secondName = new String[] { "艺昕", "红薯", "明远", "天蓬", "三丰", "德华", "歌", "佳", "乐", "天", "燕子", "子牛", "海", "燕", "花", "娟", "冰冰", "丽娅", "大为", "无为", "渔民", "大赋",
			"明", "远平", "克弱", "亦菲", "靓颖", "富城", "岳", "先觉", "牛", "阿狗", "阿猫", "辰", "蝴蝶", "文化", "冲之", "悟空", "行者", "悟净", "悟能", "观", "音", "乐天", "耀扬", "伊健", "炅", "娜", "春花", "秋香", "春香",
			"大为", "如来", "佛祖", "科比", "罗斯", "詹姆屎", "科神", "科蜜", "库里", "卡特", "麦迪", "乔丹", "魔术师", "加索尔", "法码尔", "南斯", "伊哥", "杜兰特", "保罗", "杭州", "爱湘", "湘湘", "昕", "函", "鬼谷子", "膑", "荡",
			"子家", "德利优视", "五方会谈", "来电话了", "T-IO", "Talent" ,"轨迹","超"};

	/**
	 * 
	 * @author: tanyaowu
	 */
	public UserService() {}

	/**
	 * 根据用户名和密码获取用户
	 * @param loginname
	 * @param password
	 * @return
	 * @author: tanyaowu
	 */
	public static User getUser(String loginname, String password) {
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
	 * @author: tanyaowu
	 */
	public static User getUser(String token) {
		//demo中用map，生产环境需要用cache
		User user = tokenMap.get(token);
		if (user == null) {
			user = new User();
			user.setId(UUIDSessionIdGenerator.instance.sessionId(null));
			user.setNick(familyName[RandomUtil.randomInt(0, familyName.length - 1)] + secondName[RandomUtil.randomInt(0, secondName.length - 1)]);
			user.setAvatar(nextImg());
			if (tokenMap.size() > 10000) {
				tokenMap.clear();
			}
			tokenMap.put(token, user);
		}
		return user;
	}
	public static String nextImg() {
		return ImgMnService.nextImg();
	}

	public static String newToken() {
		return UUID.randomUUID().toString();
	}
}
