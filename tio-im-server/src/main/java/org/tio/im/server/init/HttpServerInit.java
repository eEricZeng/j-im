package org.tio.im.server.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.GroupContextKey;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.handler.IHttpRequestHandler;
import org.tio.http.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.http.server.HttpServerAioHandler;
import org.tio.http.server.handler.DefaultHttpRequestHandler;
import org.tio.http.server.mvc.Routes;
import org.tio.im.server.ImServerStarter;
import org.tio.server.ServerGroupContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.guava.GuavaCache;

import com.jfinal.kit.PropKit;

/**
 * 
 * @author WChao
 *
 */
public class HttpServerInit {
	private static Logger log = LoggerFactory.getLogger(HttpServerInit.class);

	public static HttpConfig httpConfig;

	public static IHttpRequestHandler requestHandler;
	
	public static HttpServerAioHandler httpServerAioHandler;

	public static void init(ServerGroupContext serverGroupContext) throws Exception {
		long start = SystemTimer.currentTimeMillis();
		PropKit.use("app.properties");
		int port = PropKit.getInt("http.port");//启动端口
		String pageRoot = PropKit.get("http.page");//html/css/js等的根目录，支持classpath:，也支持绝对路径
		String[] scanPackages = new String[] { ImServerStarter.class.getPackage().getName() };//tio mvc需要扫描的根目录包

		httpConfig = new HttpConfig(port,null);
		httpConfig.setRoot(pageRoot);
		if (httpConfig.getSessionStore() == null) {
			GuavaCache guavaCache = GuavaCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
			httpConfig.setSessionStore(guavaCache);
		}

		if (httpConfig.getRoot() == null) {
			httpConfig.setRoot("page");
		}

		if (httpConfig.getSessionIdGenerator() == null) {
			httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
		}
		
		Routes routes = new Routes(scanPackages);
		requestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
		serverGroupContext.setAttribute(GroupContextKey.HTTP_SERVER_CONFIG, httpConfig);
		httpServerAioHandler = new HttpServerAioHandler(httpConfig, requestHandler);
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		log.info("tio Http Server初始化完毕,耗时:{}ms", iv);
	}
	/**
	 *
	 * @author tanyaowu
	 */
	public HttpServerInit() {
	}
}
