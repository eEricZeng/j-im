package org.jim.common.http.handler;

import org.jim.common.http.HttpRequest;
import org.jim.common.http.HttpResponse;
import org.jim.common.http.RequestLine;

/**
 *
 * @author wchao
 *
 */
public interface IHttpRequestHandler {
	/**
	 *
	 * @param packet
	 * @param requestLine
	 * @return
	 * @throws Exception
	 * @author wchao
	 */
	public HttpResponse handler(HttpRequest packet, RequestLine requestLine) throws Exception;

	/**
	 *
	 * @param request
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @author wchao
	 */
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine);

	/**
	 *
	 * @param request
	 * @param requestLine
	 * @param throwable
	 * @return
	 * @author wchao
	 */
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, java.lang.Throwable throwable);
	
	/**
	 * 清空静态资源缓存，如果没有缓存，可以不处理
	 * @param request
	 * @author: wchao
	 */
	public void clearStaticResCache(HttpRequest request);
}
