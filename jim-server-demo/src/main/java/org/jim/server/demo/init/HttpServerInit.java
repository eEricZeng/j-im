package org.jim.server.demo.init;

import org.jim.common.ImConfig;
import org.jim.common.http.HttpConfig;
import org.jim.server.demo.ImServerDemoStart;

import com.jfinal.kit.PropKit;

/**
 * 
 * @author WChao
 *
 */
public class HttpServerInit {

	public static void init(ImConfig imConfig) throws Exception {
		PropKit.use("app.properties");
		String pageRoot = PropKit.get("http.page");//html/css/js等的根目录，支持classpath:，也支持绝对路径
		String[] scanPackages = new String[] { ImServerDemoStart.class.getPackage().getName() };//t-im mvc需要扫描的根目录包
		HttpConfig httpConfig = new HttpConfig();
		httpConfig.setPageRoot(pageRoot);//设置web访问路径;
		httpConfig.setMaxLiveTimeOfStaticRes(0);//不缓存资源;
		httpConfig.setScanPackages(scanPackages);//设置tio mvc扫描目录;
		imConfig.setHttpConfig(httpConfig);
	}
	/**
	 * @author WChao
	 * 
	 */
	public HttpServerInit() {
		
	}
}
