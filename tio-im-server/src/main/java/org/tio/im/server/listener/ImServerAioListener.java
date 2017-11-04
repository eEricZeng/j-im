package org.tio.im.server.listener;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImServerAioListener implements ServerAioListener {

	Logger logger = Logger.getLogger(ImServerAioListener.class);
	
	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public ImServerAioListener() {
	}
	
	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public static void main(String[] args) {
	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		/*ImSessionContext imSessionContext = new ImSessionContext();
		channelContext.setAttribute(imSessionContext);
		//GroupContext groupContext = channelContext.getGroupContext();
		int permitsPerSecond = ImServerStarter.conf.getInt("request.permitsPerSecond");
		int warnClearInterval = 1000 * ImServerStarter.conf.getInt("request.warnClearInterval");
		int maxWarnCount = ImServerStarter.conf.getInt("request.maxWarnCount");
		int maxAllWarnCount = ImServerStarter.conf.getInt("request.maxAllWarnCount");
		RateLimiterWrap rateLimiterWrap = new RateLimiterWrap(permitsPerSecond, warnClearInterval, maxWarnCount, maxAllWarnCount);

		imSessionContext.setRequestRateLimiter(rateLimiterWrap);*/

		/*if (isConnected) {
			Aio.bindUser(channelContext, channelContext.getId());
		}*/
		return;
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onBeforeSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @author: tanyaowu
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
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterReceived(ChannelContext channelContext, Packet packet, int packetSize) {
		/*if (packet.getBody() == null) {
			Aio.remove(channelContext, "body is null");
		}*/
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterClose(org.tio.core.ChannelContext, java.lang.Throwable, java.lang.String)
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author: tanyaowu
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
