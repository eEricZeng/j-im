/*package org.tio.im.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.AuthReqBody;
import org.tio.im.common.packets.AuthRespBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.DeviceType;
import org.tio.im.server.command.ImBsHandlerIntf;

*//**
 * 
 * 
 * @author tanyaowu 
 *
 *//*
public class AuthReqHandler implements ImBsHandlerIntf
{
	*//**
	 * 
	 *//*
	public AuthReqHandler()
	{

	}

	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception
	{
		if (packet.getBody() == null)
		{
			throw new Exception("body is null");
		}	
		
		AuthReqBody authReqBody = AuthReqBody.parseFrom(packet.getBody());
		String token = authReqBody.getToken();
		String deviceId = authReqBody.getDeviceId();
		String deviceInfo = authReqBody.getDeviceInfo();
		Long seq = authReqBody.getSeq();
		DeviceType deviceType = authReqBody.getDeviceType();
		String sign = authReqBody.getSign();

		if (StringUtils.isBlank(deviceId))
		{
			Aio.close(channelContext, "did is null");
			return null;
		}

		if (seq == null || seq <= 0)
		{
			Aio.close(channelContext, "seq is null");
			return null;
		}

		token = token == null ? "" : token;
		deviceInfo = deviceInfo == null ? "" : deviceInfo;

		String data = token + deviceId + deviceInfo + seq + org.tio.im.common.Const.authkey;
System.out.println(data+"=="+deviceType+"=="+sign);
//		try
//		{
//			String _sign = Md5.getMD5(data);
//			if (!_sign.equals(sign))
//			{
//				log.error("sign is invalid, {}, actual sign:{},expect sign:{}", channelContext.toString(), sign, _sign);
//				Aio.close(channelContext, "sign is invalid");
//				return null;
//			}
//		} catch (Exception e)
//		{
//			log.error(e.toString(), e);
//			Aio.close(channelContext, e.getMessage());
//			return null;
//		}

		

		ImPacket imRespPacket = new ImPacket();
		AuthRespBody authRespBody = AuthRespBody.newBuilder().build();
		imRespPacket.setCommand(Command.COMMAND_AUTH_RESP);
		imRespPacket.setBody(authRespBody.toByteArray());
		Aio.send(channelContext, imRespPacket);
		return null;
	}



	*//**
	 * @param args
	 * @throws Exception 
	 *//*
	public static void main(String[] args) throws Exception
	{
		
	}

	@Override
	public Command command() {
		return Command.COMMAND_AUTH_REQ;
	}
}
*/