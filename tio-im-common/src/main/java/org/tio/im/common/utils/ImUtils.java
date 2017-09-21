package org.tio.im.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.Client;
import org.tio.utils.lock.SetWithLock;
/**
 * @author tanyaowu 
 * 2017年5月5日 下午5:35:02
 */
public class ImUtils {

	/**
	 * 设置Client对象到ImSessionContext中
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public static Client setClient(ChannelContext channelContext) {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		Client client = imSessionContext.getClient();
		if (client == null) {
			client = new Client();
			client.setId(channelContext.getId());
			client.setIp(channelContext.getClientNode().getIp());
			client.setPort(channelContext.getClientNode().getPort());
			imSessionContext.setClient(client);
		}

		return client;
	}

	public static String formatRegion(String region) {
		if (StringUtils.isBlank(region)) {
			return "";
		}

		String[] arr = StringUtils.split(region, "|");//.split("|");
		List<String> validArr = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			String e = arr[i];
			if (StringUtils.isNotBlank(e) && !"0".equals(e)) {
				validArr.add(e);
			}
		}
		if (validArr.size() == 0) {
			return "";
		} else if (validArr.size() == 1) {
			return validArr.get(0);
		} else {
			return validArr.get(validArr.size() - 2) + validArr.get(validArr.size() - 1);
		}
	}
	/**
	 * 
		 * 功能描述：[根据用户ID获取当前用户]
		 * 创建者：WChao 创建时间: 2017年9月18日 下午4:34:39
		 * @param groupContext
		 * @param userid
		 * @return
		 *
	 */
	public static Client getUser(GroupContext groupContext,String userid){
		ChannelContext channelContext = Aio.getChannelContextByUserid(groupContext, userid);
		if(channelContext != null){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			Client client = imSessionContext.getClient();
			return client;
		}
		return null;
	}
	/**
	 * 
		 * 功能描述：[获取所有用户(在线+离线)]
		 * 创建者：WChao 创建时间: 2017年9月18日 下午4:31:54
		 * @param groupContext
		 * @return
		 *
	 */
	public static List<Client> getAllUser(GroupContext groupContext){
		List<Client> clients = new ArrayList<Client>();
		SetWithLock<ChannelContext> allChannels = Aio.getAllChannelContexts(groupContext);
		Set<ChannelContext> allChannelSets = allChannels.getObj();
		if(allChannelSets == null)
			return clients;
		for(ChannelContext channelContext : allChannelSets){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			Client client = imSessionContext.getClient();
			if(client != null)
			clients.add(client);
		}
		return clients;
	}
	/**
	 * 
		 * 功能描述：[获取所有在线用户]
		 * 创建者：WChao 创建时间: 2017年9月18日 下午4:31:42
		 * @param groupContext
		 * @return
		 *
	 */
	public static List<Client> getAllOnlineUser(GroupContext groupContext){
		List<Client> clients = new ArrayList<Client>();
		SetWithLock<ChannelContext> allChannels = Aio.getAllConnectedsChannelContexts(groupContext);
		Set<ChannelContext> allChannelSets = allChannels.getObj();
		if(allChannelSets == null)
			return clients;
		for(ChannelContext channelContext : allChannelSets){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			if(imSessionContext != null){
				Client client = imSessionContext.getClient();
				if(client != null)
				clients.add(client);
			}
		}
		return clients;
	}
	
	/**
	 * 
		 * 功能描述：[发送到群组里的所有不同协议端]
		 * 创建者：WChao 创建时间: 2017年9月21日 下午3:26:57
		 * @param groupContext
		 * @param group
		 * @param packet
		 *
	 */
	public static void sendToGroup(GroupContext groupContext, String group, ImPacket packet){
		if(packet.getBody() == null)
			return;
		SetWithLock<ChannelContext> withLockChannels = Aio.getChannelContextsByGroup(groupContext, group);
		Set<ChannelContext> channels = withLockChannels.getObj();
		if(channels.size() > 0){
			for(ChannelContext channelContext : channels){
				ImPacket respPacket = Resps.convertPacket(packet.getBody(), channelContext);
				Aio.sendToId(groupContext,channelContext.getId(), respPacket);
			}
		}
	}
	
	public static String formatUserAgent(ChannelContext channelContext) {
	/*	ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		HttpRequestPacket httpHandshakePacket = imSessionContext.getHandshakeRequestPacket();
		
		if (httpHandshakePacket != null) {
			UserAgent userAgent = httpHandshakePacket.getUserAgent();

			String DeviceName = userAgent.getValue(UserAgent.DEVICE_NAME);//StringUtils.leftPad(userAgent.getValue(UserAgent.DEVICE_NAME), 1);
			String DeviceCpu = userAgent.getValue("DeviceCpu"); //StringUtils.leftPad(userAgent.getValue("DeviceCpu"), 1);
			String OperatingSystemNameVersion = userAgent.getValue("OperatingSystemNameVersion"); //StringUtils.leftPad(userAgent.getValue("OperatingSystemNameVersion"), 1);
			String AgentNameVersion = userAgent.getValue("AgentNameVersion");//StringUtils.leftPad(userAgent.getValue("AgentNameVersion"), 1);
			String useragentStr = DeviceName + " " + DeviceCpu + " " + OperatingSystemNameVersion + " " + AgentNameVersion;

			return useragentStr;
		} else {
			return "";
		}*/
		
		return null;
	}
	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {
		String region = "中国|杭州|铁通";
		String xx = formatRegion(region);
		System.out.println(xx);
	}
}
