/**
 * 
 */
package org.tio.im.server.command.handler.proc;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.HandshakeBody;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.tcp.TcpPacket;
import org.tio.im.common.tcp.TcpSessionContext;
import org.tio.im.common.utils.Resps;
import org.tio.im.server.command.handler.ChatReqHandler;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午8:11:34
 */
public class TcpProCmdHandler implements ProCmdHandlerIntf {

	@Override
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		RespBody handshakeBody = new RespBody(Command.COMMAND_HANDSHAKE_RESP).setData(new HandshakeBody().setHbyte(Protocol.HANDSHAKE_BYTE));
		ImPacket handshakePacket = Resps.convertRespPacket(handshakeBody.toByte(), Command.COMMAND_HANDSHAKE_RESP, channelContext);
		return handshakePacket;
	}

	
	@Override
	public ImPacket chat(ImPacket packet, ChannelContext channelContext) throws Exception {
		TcpPacket tcpPacket = (TcpPacket)packet;
		ChatBody chatBody = ChatReqHandler.parseChatBody(tcpPacket.getBody(), channelContext);
		ImPacket resChatPacket = ChatReqHandler.convertChatResPacket(chatBody, channelContext);
		ChannelContext toChnnelContext = ChatReqHandler.getToChannel(chatBody, channelContext.getGroupContext());
		if(resChatPacket.getStatus() == ImStatus.C1){
			Aio.send(toChnnelContext, resChatPacket);
			RespBody respBody = new RespBody().setCode(ImStatus.C1.getCode()).setCommand(Command.COMMAND_CHAT_RESP).setMsg(ImStatus.C1.getText());
			return Resps.convertRespPacket(respBody, channelContext);
		}else{
			return resChatPacket;
		}
	}

	
	@Override
	public boolean isProtocol(ChannelContext channelContext) throws Exception {
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){
			return false;
		}else if(sessionContext instanceof TcpSessionContext){
			return true;
		}
		return false;
	}
}
