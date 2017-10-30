package org.tio.im.server.handler;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.command.handler.AuthReqHandler;
import org.tio.im.server.command.handler.ChatReqHandler;
import org.tio.im.server.command.handler.CloseReqHandler;
import org.tio.im.server.command.handler.HandshakeReqHandler;
import org.tio.im.server.command.handler.HeartbeatReqHandler;
import org.tio.im.server.command.handler.JoinGroupReqHandler;
import org.tio.im.server.command.handler.LoginReqHandler;
import org.tio.im.server.command.handler.UserReqHandler;
import org.tio.im.server.command.handler.proc.TcpProCmdHandler;
import org.tio.im.server.command.handler.proc.WsProCmdHandler;
import org.tio.im.server.http.HttpServerHandler;
import org.tio.im.server.tcp.TcpServerHandler;
import org.tio.im.server.ws.WsServerHandler;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
/**
 * 
 * @author tanyaowu 
 *
 */
public class ImServerAioHandler implements ServerAioHandler {

	private  CommandManager commandManager = CommandManager.getInstance();
	private  ServerHandlerManager serverHandlerManager = ServerHandlerManager.getInstance();
	
	public void init(ServerGroupContext serverGroupContext) {
		commandManager
		.registerCommand(new HandshakeReqHandler())
		.registerCommand(new AuthReqHandler())
		.registerCommand(new LoginReqHandler())
		.registerCommand(new ChatReqHandler())
		.registerCommand(new JoinGroupReqHandler())
		.registerCommand(new HeartbeatReqHandler())
		.registerCommand(new CloseReqHandler())
		.registerCommand(new UserReqHandler())
		//添加不同协议的Cmd命令处理器;
		.addProCmdHandler(new WsProCmdHandler())
		.addProCmdHandler(new TcpProCmdHandler());
		
		serverHandlerManager
		.addServerHandler(new HttpServerHandler())
		.addServerHandler(new TcpServerHandler())
		.addServerHandler(new WsServerHandler())
		.init(serverGroupContext);
	}
	/** 
	 * @see org.tio.core.intf.AioHandler#handler(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @throws Exception 
	 * @author: Wchao
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		AbServerHandler handler = serverHandlerManager.getServerHandler(null,channelContext);
		if(handler != null){
			handler.handler(packet, channelContext);
		}
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#encode(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @author: Wchao
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		AbServerHandler handler = serverHandlerManager.getServerHandler(null,channelContext);
		if(handler != null){
			return handler.encode(packet, groupContext, channelContext);
		}
		return null;
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#decode(java.nio.ByteBuffer)
	 * 
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 * @author: Wchao
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public Packet decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		AbServerHandler handler = serverHandlerManager.getServerHandler(buffer,channelContext);
		if(handler != null){
			return handler.decode(buffer, channelContext);
		}
		return null;
	}
}
