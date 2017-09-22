package org.tio.im.server.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.http.GroupContextKey;
import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.http.handler.IHttpRequestHandler;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.im.server.BootStarter;
import org.tio.im.server.http.DefaultHttpRequestHandler;
import org.tio.im.server.http.mvc.Routes;
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

	public static void init(ServerGroupContext serverGroupContext) throws Exception {
		long start = SystemTimer.currentTimeMillis();
		PropKit.use("app.properties");
		int port = PropKit.getInt("port");//启动端口
		String pageRoot = PropKit.get("http.page");//html/css/js等的根目录，支持classpath:，也支持绝对路径
		String[] scanPackages = new String[] { BootStarter.class.getPackage().getName() };//tio mvc需要扫描的根目录包

		httpConfig = new HttpConfig(port,null);
		httpConfig.setPageRoot(pageRoot);
		httpConfig.setMaxLiveTimeOfStaticRes(0);//不缓存资源;
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
		
		Routes routes = new Routes(scanPackages);
		requestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
		serverGroupContext.setAttribute(GroupContextKey.HTTP_SERVER_CONFIG, httpConfig);
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
