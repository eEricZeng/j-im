package org.jim.common.http;

import org.jim.common.config.Config;
import org.jim.common.http.handler.IHttpRequestHandler;
import org.jim.common.http.listener.IHttpServerListener;
import org.jim.common.session.id.ISessionIdGenerator;
import org.tio.utils.cache.ICache;

/**
 * @author wchao
 * 2017年8月15日 下午1:21:14
 */
public class HttpConfig extends Config{

	//	private static Logger log = LoggerFactory.getLogger(HttpConfig.class);

	/**
	 * 存放HttpSession对象的cacheName
	 */
	public static final String SESSION_CACHE_NAME = "tio-h-s";

	/**
	 * 存放sessionId的cookie name
	 */
	public static final String SESSION_COOKIE_NAME = "TwIxO";

	/**
	 * session默认的超时时间，单位：秒
	 */
	public static final long DEFAULT_SESSION_TIMEOUT = 30 * 60;

	/**
	 * 默认的静态资源缓存时间，单位：秒
	 */
	public static final int MAX_LIVETIME_OF_STATICRES = 60 * 10;
	
	/**
	 * 文件上传时，boundary值的最大长度
	 */
	public static final int MAX_LENGTH_OF_BOUNDARY = 256;
	
	/**
	 * 文件上传时，头部的最大长度
	 */
	public static final int MAX_LENGTH_OF_MULTI_HEADER = 128;
	
	/**
	 * 文件上传时，体的最大长度
	 */
	public static final int MAX_LENGTH_OF_MULTI_BODY = 1024 * 1024 * 20;

	/**
	 * @param args
	 * @author wchao
	 */
	public static void main(String[] args) {

	}

	private String serverInfo = HttpConst.SERVER_INFO;

	private String charset = HttpConst.CHARSET_NAME;

	private ICache sessionStore = null;

	/**
	 * 存放HttpSession对象的cacheName

	 */
	private String sessionCacheName = SESSION_CACHE_NAME;

	/**
	 * session超时时间，单位：秒
	 */
	private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

	private String sessionCookieName = SESSION_COOKIE_NAME;

	/**
	 * 静态资源缓存时间，如果小于等于0则不缓存，单位：秒
	 */
	private int maxLiveTimeOfStaticRes = MAX_LIVETIME_OF_STATICRES;

	private String page404 = "/404.html";

	private String page500 = "/500.html";

	private ISessionIdGenerator sessionIdGenerator;
	
	private IHttpRequestHandler httpRequestHandler;
	
	private IHttpServerListener httpServerListener;

	/**
	 * 示例：
	 * 1、classpath中：page
	 * 2、绝对路径：/page
	 * //FileUtil.getAbsolutePath("page");//"/page";
	 */
	private String pageRoot = null;
	/**
	 * mvc扫描包路径;
	 */
	private String[] scanPackages = null;

	
	public HttpConfig() {}
	
	/**
	 *
	 * @author wchao
	 */
	public HttpConfig(Integer bindPort, Long sessionTimeout) {
		this.bindPort = bindPort;
		if (sessionTimeout != null) {
			this.sessionTimeout = sessionTimeout;
		}
	}
	
	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the maxLiveTimeOfStaticRes
	 */
	public int getMaxLiveTimeOfStaticRes() {
		return maxLiveTimeOfStaticRes;
	}

	public String getPage404() {
		return page404;
	}

	public String getPage500() {
		return page500;
	}

	/**
	 * @return the pageRoot
	 */
	public String getPageRoot() {
		return pageRoot;
	}

	/**
	 * @return the serverInfo
	 */
	public String getServerInfo() {
		return serverInfo;
	}

	/**
	 * @return the sessionCacheName
	 */
	public String getSessionCacheName() {
		return sessionCacheName;
	}

	public String getSessionCookieName() {
		return sessionCookieName;
	}

	public ISessionIdGenerator getSessionIdGenerator() {
		return sessionIdGenerator;
	}

	public ICache getSessionStore() {
		return sessionStore;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param maxLiveTimeOfStaticRes the maxLiveTimeOfStaticRes to set
	 */
	public void setMaxLiveTimeOfStaticRes(int maxLiveTimeOfStaticRes) {
		this.maxLiveTimeOfStaticRes = maxLiveTimeOfStaticRes;
	}

	public void setPage404(String page404) {
		this.page404 = page404;
	}

	public void setPage500(String page500) {
		this.page500 = page500;
	}

	/**
	 * 
	 * @param pageRoot
	 * @author wchao
	 */
	public void setPageRoot(String pageRoot) {
		this.pageRoot = pageRoot;//FileUtil.getAbsolutePath(root);//"/page";;
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	/**
	 * @param sessionCacheName the sessionCacheName to set
	 */
	public void setSessionCacheName(String sessionCacheName) {
		this.sessionCacheName = sessionCacheName;
	}

	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public void setSessionIdGenerator(ISessionIdGenerator sessionIdGenerator) {
		this.sessionIdGenerator = sessionIdGenerator;
	}

	public void setSessionStore(ICache sessionStore) {
		this.sessionStore = sessionStore;
		//		this.httpSessionManager = HttpSessionManager.getInstance(sessionStore);
	}

	/**
	 * @return the httpRequestHandler
	 */
	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	/**
	 * @param httpRequestHandler the httpRequestHandler to set
	 */
	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}

	public String[] getScanPackages() {
		return scanPackages;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

	public IHttpServerListener getHttpServerListener() {
		return httpServerListener;
	}

	public void setHttpServerListener(IHttpServerListener httpServerListener) {
		this.httpServerListener = httpServerListener;
	}

}
