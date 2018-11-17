package org.jim.common.listener;

import org.tio.core.ChannelContext;

/**
 * IM绑定用户及群组监听器;
 * @author WChao
 * @date 2018年4月8日 下午4:09:14
 */
public interface ImBindListener {
	/**
	 * 绑定群组后回调该方法
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 */
	void onAfterGroupBind(ChannelContext channelContext, String group) throws Exception;

	/**
	 * 解绑群组后回调该方法
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 */
	void onAfterGroupUnbind(ChannelContext channelContext, String group) throws Exception;
	/**
	 * 绑定用户后回调该方法
	 * @param channelContext
	 * @param userId
	 * @throws Exception
	 */
	void onAfterUserBind(ChannelContext channelContext, String userId) throws Exception;

	/**
	 * 解绑用户后回调该方法
	 * @param channelContext
	 * @param userId
	 * @throws Exception
	 */
	void onAfterUserUnbind(ChannelContext channelContext, String userId) throws Exception;
	/**
	 * 更新用户终端协议类型及在线状态;
	 * @param channelContext
	 * @param terminal(ws、tcp、http、android、ios等)
	 * @param status(online、offline)
	 */
    void initUserTerminal(ChannelContext channelContext , String terminal , String status);
}
