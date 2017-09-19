/**
 * 
 */
package org.tio.im.server.command.handler.proc;

import java.nio.ByteBuffer;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImStatus;
import org.tio.im.common.Protocol;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.tcp.TcpPacket;
import org.tio.im.common.tcp.TcpSessionContext;
import org.tio.im.server.command.handler.ChatReqHandler;
import org.tio.im.server.util.Resps;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午8:11:34
 */
public class TcpProCmdHandler implements ProCmdHandlerIntf {

	@Override
	public ImPacket handshake(ImPacket packet, ChannelContext channelContext) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put(Protocol.HANDSHAKE_BYTE);
		ImPacket handshakeResPacket = new ImPacket(Command.COMMAND_HANDSHAKE_RESP,buffer.array());
		return handshakeResPacket;
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
			return Resps.convertPacket(respBody, channelContext);
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


	@Override
	public ImPacket heartbeat(ImPacket packet, ChannelContext channelContext) throws Exception {
		
		return null;
	}

}
