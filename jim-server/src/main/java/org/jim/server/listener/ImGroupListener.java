package org.jim.server.listener;

import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;
/**
 * 绑定群组监听器
 * @author WChao 
 * 2017年5月13日 下午10:38:36
 */
public class ImGroupListener implements GroupListener{

	/**
	 * 默认构造器
	 */
	public ImGroupListener() {}

	/**
	 * 绑定回调方法
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 */
	@Override
	public void onAfterBind(ChannelContext channelContext, String group) throws Exception {
	}

	/**
	 * 解绑回调方法
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 */
	@Override
	public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
	}
}
