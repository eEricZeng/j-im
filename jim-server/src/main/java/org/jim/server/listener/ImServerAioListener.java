package org.jim.server.listener;

import org.apache.log4j.Logger;
import org.jim.common.Const;
import org.jim.common.ImConfig;
import org.jim.common.ImSessionContext;
import org.jim.common.message.IMesssageHelper;
import org.jim.common.packets.Client;
import org.jim.common.packets.User;
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
	
	private ImConfig imConfig;
	
	/**
	 * @author: WChao
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public ImServerAioListener() {}
	public ImServerAioListener(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
	/**
	 * 
	 * 建链后触发本方法，注：建链不一定成功，需要关注参数isConnected
	 * @param channelContext
	 * @param isConnected 是否连接成功,true:表示连接成功，false:表示连接失败
	 * @param isReconnect 是否是重连, true: 表示这是重新连接，false: 表示这是第一次连接
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		return;
	}

	/**
	 * 消息包发送之后触发本方法
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess true:发送成功，false:发送失败
	 * @throws Exception
	 * @author WChao
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
	}
	/**
	 * 连接关闭前触发本方法
	 * @param channelContext the channelcontext
	 * @param throwable the throwable 有可能为空
	 * @param remark the remark 有可能为空
	 * @param isRemove
	 * @author WChao
	 * @throws Exception 
	 */
	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
		IMesssageHelper messageHelper = imConfig.getMessageHelper();
		if(messageHelper != null){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			if(imSessionContext == null)
				return;
			Client client = imSessionContext.getClient();
			if(client == null)
				return;
			User onlineUser = client.getUser();
			if(onlineUser == null)
				return;
			messageHelper.getBindListener().initUserTerminal(channelContext, onlineUser.getTerminal(), Const.OFFLINE);
		}
	}
	/**
	 * 解码成功后触发本方法
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet,int packetSize) throws Exception {
		
	}
	/**
	 * 接收到TCP层传过来的数据后
	 * @param channelContext
	 * @param receivedBytes 本次接收了多少字节
	 * @throws Exception
	 */
	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext,int receivedBytes) throws Exception {
		
	}
	/**
	 * 处理一个消息包后
	 * @param channelContext
	 * @param packet
	 * @param cost 本次处理消息耗时，单位：毫秒
	 * @throws Exception
	 */
	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet,long cost) throws Exception {
		
	}

}
