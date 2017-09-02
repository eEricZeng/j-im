package org.tio.im.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpConst;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.packets.ChatReqBody;
import org.tio.im.common.packets.ChatRespBody;
import org.tio.im.common.packets.Client;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author WChao
 *
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

	public static String formatUserAgent(ChannelContext channelContext) {
		/*WsSessionContext imSessionContext = (WsSessionContext)channelContext.getAttribute();
		HttpRequest httpHandshakePacket = imSessionContext.getHandshakeRequestPacket();
		
		if (httpHandshakePacket != null) {
			UserAgent userAgent = httpHandshakePacket.getu

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
	 * 格式化状态码消息响应体;
	 * @param status
	 * @return
	 */
	public static byte[] toChatRespBody(ImStatus status){
		return JSONObject.toJSONBytes(new ChatRespBody().setErrorCode(status.getCode()).setErrorMsg(status.getDescription()+" "+status.getText()));
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	public static ChatReqBody parseChatBody(byte[] body){
		if(body == null)
			return null;
		ChatReqBody chatReqBody = null;
		try{
			String text = new String(body,HttpConst.CHARSET_NAME);
		    chatReqBody = JSONObject.parseObject(text,ChatReqBody.class);
			if(chatReqBody != null)
				return chatReqBody;
		}catch(Exception e){
			
		}
		return chatReqBody;
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	public static ChatReqBody parseChatBody(String bodyStr){
		if(bodyStr == null)
			return null;
		try {
			return parseChatBody(bodyStr.getBytes(HttpConst.CHARSET_NAME));
		} catch (Exception e) {
			e.printStackTrace();
		}
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
