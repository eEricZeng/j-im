package org.tio.im.server.util;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.im.common.http.GroupContextKey;
import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.http.HttpRequest;
/**
 * @author tanyaowu
 * 2017年8月18日 下午5:47:00
 */
public class HttpServerUtils {
	/**
	 *
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	public static HttpConfig getHttpConfig(HttpRequest request) {
		ChannelContext channelContext = request.getChannelContext();
		GroupContext groupContext = channelContext.getGroupContext();
		HttpConfig httpConfig = (HttpConfig) groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG);
		return httpConfig;
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public HttpServerUtils() {
	}
}
