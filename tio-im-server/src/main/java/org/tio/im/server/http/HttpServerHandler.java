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
import org.tio.im.common.Const;
import org.tio.im.common.ImConfig;
import org.tio.im.common.Protocol;
import org.tio.im.common.http.GroupContextKey;
import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.http.HttpRequest;
import org.tio.im.common.http.HttpRequestDecoder;
import org.tio.im.common.http.HttpResponse;
import org.tio.im.common.http.HttpResponseEncoder;
import org.tio.im.common.http.handler.IHttpRequestHandler;
import org.tio.im.common.http.session.HttpSession;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.im.server.ImServerStarter;
import org.tio.im.server.handler.AbServerHandler;
import org.tio.im.server.http.mvc.Routes;
import org.tio.server.ServerGroupContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.guava.GuavaCache;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月3日 下午3:07:54
 */
public class HttpServerHandler extends AbServerHandler{
	
	private Logger log = LoggerFactory.getLogger(HttpServerHandler.class);

	private HttpConfig httpConfig;
	
	private IHttpRequestHandler httpRequestHandler;
	
	public HttpServerHandler() {}
	
	public HttpServerHandler(HttpConfig httpServerConfig){
		this.httpConfig = httpServerConfig;
	}
	@Override
	public void init(ServerGroupContext serverGroupContext,ImConfig imConfig)throws Exception{
		long start = SystemTimer.currentTimeMillis();
		this.httpConfig = imConfig.getHttpConfig();
		if (httpConfig.getSessionStore() == null) {
			GuavaCache guavaCache = GuavaCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
			httpConfig.setSessionStore(guavaCache);
		}
		if (httpConfig.getPageRoot() == null) {
			httpConfig.setPageRoot("page");
		}
		if (httpConfig.getSessionIdGenerator() == null) {
			httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
		}
		if(httpConfig.getScanPackages() == null){
			String[] scanPackages = new String[] { ImServerStarter.class.getPackage().getName() };//t-im mvc需要扫描的根目录包
			httpConfig.setScanPackages(scanPackages);
		}else{
			String[] scanPackages = new String[httpConfig.getScanPackages().length+1];
			scanPackages[0] = ImServerStarter.class.getPackage().getName();
			System.arraycopy(httpConfig.getScanPackages(), 0, scanPackages, 1, httpConfig.getScanPackages().length);
			httpConfig.setScanPackages(scanPackages);
		}
		Routes routes = new Routes(httpConfig.getScanPackages());
		httpRequestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
		httpConfig.setHttpRequestHandler(httpRequestHandler);
		serverGroupContext.setAttribute(GroupContextKey.HTTP_SERVER_CONFIG, httpConfig);
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		log.info("t-im Http Server初始化完毕,耗时:{}ms", iv);
	}
	
	@Override
	public boolean isProtocol(ByteBuffer buffer,ChannelContext channelContext)throws Throwable{
		Object sessionContext = channelContext.getAttribute();
		if(sessionContext == null){
			if(buffer != null){
				HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
				if(request.getHeaders().get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key) == null)
				{
					channelContext.setAttribute(new HttpSession());
					return true;
				}
			}
		}else if(sessionContext instanceof HttpSession){
			return true;
		}
		return false;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext,ChannelContext channelContext) {
		HttpResponse httpResponsePacket = (HttpResponse) packet;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponsePacket, groupContext, channelContext,false);
		return byteBuffer;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext)throws Exception {
		HttpRequest httpRequestPacket = (HttpRequest) packet;
		HttpResponse httpResponsePacket = httpRequestHandler.handler(httpRequestPacket, httpRequestPacket.getRequestLine());
		Aio.send(channelContext, httpResponsePacket);
	}

	@Override
	public Packet decode(ByteBuffer buffer, ChannelContext channelContext)throws AioDecodeException {
		HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
		channelContext.setAttribute(Const.HTTP_REQUEST,request);
		return request;
	}
	
	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
	
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	@Override
	public String name() {
		
		return Protocol.HTTP;
	}
	
}
