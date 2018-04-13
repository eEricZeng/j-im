package org.jim.common.utils;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImSessionContext;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Client;
/**
 * @author wchao 
 * 2017年5月5日 下午5:35:02
 */
public class ImUtils {

	/**
	 * 设置Client对象到ImSessionContext中
	 * @param channelContext
	 * @return
	 * @author: wchao
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
	 * @author: wchao
	 */
	public static void main(String[] args) {
		String region = "中国|杭州|铁通";
		String xx = formatRegion(region);
		System.out.println(xx);
	}
}
