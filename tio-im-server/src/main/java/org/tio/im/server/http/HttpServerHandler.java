/**
 * 
 */
package org.tio.im.server.http;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.im.common.ImPacketType;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpPacket;
import org.tio.im.common.http.HttpRequestDecoder;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponseEncoder;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.Method;
import org.tio.im.common.packets.Command;
import org.tio.im.server.ImServerStarter;
import org.tio.im.server.command.CommandManager;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.im.server.http.mvc.Routes;

import com.jfinal.kit.PropKit;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午3:07:54
 */
public class HttpServerHandler extends AbServerHandler{

	private static Logger log = LoggerFactory.getLogger(HttpServerHandler.class);

	protected HttpServerConfig httpServerConfig;
	
	private IHttpRequestHandler httpRequestHandler;
	
	private Packet packet;
	
	private CommandManager commandManager = CommandManager.getInstance();
	public HttpServerHandler() {}
	
	public HttpServerHandler(IHttpRequestHandler httpRequestHandler , HttpServerConfig httpServerConfig){
		this.httpRequestHandler = httpRequestHandler;
		this.httpServerConfig = httpServerConfig;
	}
	@Override
	public void init() {
		PropKit.use("app.properties");
		
		int port = PropKit.getInt("port");//启动端口
		String pageRoot = PropKit.get("page.root");//html/css/js等的根目录，支持classpath:，也支持绝对路径
		String[] scanPackages = new String[] { ImServerStarter.class.getPackage().getName() };//tio mvc需要扫描的根目录包

		HttpServerConfig httpServerConfig = new HttpServerConfig(port);
		httpServerConfig.setRoot(pageRoot);

		Routes routes = new Routes(scanPackages);
		this.httpRequestHandler = new DefaultHttpRequestHandler(httpServerConfig, routes);
		this.httpServerConfig = httpServerConfig;
		log.info("HttpServerHandler初始化完毕...");
	}
	
	public boolean isHttpMethod(ByteBuffer buffer)throws Exception{
		for(Method method : Method.values()){
			String value = method.getValue();
			byte[] values = new byte[value.length()];
			for(int i = 0 ;i<values.length ; i++){
				values[i] = buffer.get(i);
			}
			String rqMethod = new String(values,HttpConst.CHARSET_NAME);
			if(value.equals(rqMethod)){
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isProtocol(ByteBuffer buffer,Packet packet,ChannelContext channelContext)throws Throwable{
		ImSessionContext sessionContext = (ImSessionContext)channelContext.getAttribute();
		if(ImPacketType.HTTP == sessionContext.getPacketType())
			return true;
		if(buffer != null){
			if(isHttpMethod(buffer)){
				HttpRequestPacket httpRequestPacket = HttpRequestDecoder.decode(buffer,false);
				if(httpRequestPacket.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) == null){
					return true;
				}
			} 
		}else if(packet != null && packet instanceof HttpPacket){
			HttpPacket httpPacket = (HttpPacket)packet;
			if(httpPacket.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) == null){
				return true;
			}
		}
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext,ChannelContext channelContext) {
		HttpResponsePacket httpResponsePacket = (HttpResponsePacket) packet;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponsePacket, groupContext, channelContext);
		return byteBuffer;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		HttpRequestPacket httpRequestPacket = (HttpRequestPacket) packet;
		HttpResponsePacket httpResponsePacket = httpRequestHandler.handler(httpRequestPacket, httpRequestPacket.getRequestLine(), channelContext);
		Aio.send(channelContext, httpResponsePacket);
	}

	@Override
	public Packet decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
		imSessionContext.setPacketType(ImPacketType.HTTP);
		HttpRequestPacket httpRequestPacket = HttpRequestDecoder.decode(buffer,true);
		String cmd = httpRequestPacket.getHeaders().get(Protocol.COMMAND);
		Command command = commandManager.getCommand(cmd);
		if(command == null){
			command = Command.COMMAND_CHAT_REQ;
		}
		httpRequestPacket.setCommand(command);
		return httpRequestPacket;
	}
	
	@Override
	public AbServerHandler build() {
		return new HttpServerHandler(this.getHttpRequestHandler(),this.getHttpServerConfig());
	}
	
	public HttpServerConfig getHttpServerConfig() {
		return httpServerConfig;
	}

	public void setHttpServerConfig(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}

	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}

	public Packet getPacket() {
		return packet;
	}

	public HttpServerHandler setPacket(Packet packet) {
		this.packet = packet;
		return this;
	}

	@Override
	public String name() {
		
		return Protocol.HTTP;
	}
	
}
