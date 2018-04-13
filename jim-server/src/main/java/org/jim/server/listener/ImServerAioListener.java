package org.jim.server.listener;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author WChao 
 *
 */
public class ImServerAioListener implements ServerAioListener {

	Logger logger = Logger.getLogger(ImServerAioListener.class);
	
	/**
	 * 
	 *
	 * @author: WChao
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public ImServerAioListener() {
	}
	
	/**
	 * @param args
	 *
	 * @author: WChao
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public static void main(String[] args) {
	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		return;
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onBeforeSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @author: WChao
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterReceived(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author: WChao
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterReceived(ChannelContext channelContext, Packet packet, int packetSize) {
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterClose(org.tio.core.ChannelContext, java.lang.Throwable, java.lang.String)
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author: WChao
	 * 2017年2月1日 上午11:03:11
	 * 
	 */
	@Override
	public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

}
