package org.tio.im.server.http;

import org.tio.core.ChannelContext;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.RequestLine;
/**
 * 
 * @author tanyaowu 
 *
 */
public interface IHttpRequestHandler
{
	/**
	 * 
	 * @param packet
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public HttpResponsePacket handler(HttpRequestPacket packet, RequestLine requestLine, ChannelContext channelContext)  throws Exception;
	
	/**
	 * 
	 * @param httpRequestPacket
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public HttpResponsePacket resp404(HttpRequestPacket httpRequestPacket, RequestLine requestLine, ChannelContext channelContext);
	
	/**
	 * 
	 * @param httpRequestPacket
	 * @param requestLine
	 * @param channelContext
	 * @param throwable
	 * @return
	 * @author: tanyaowu
	 */
	public HttpResponsePacket resp500(HttpRequestPacket httpRequestPacket, RequestLine requestLine, ChannelContext channelContext, java.lang.Throwable throwable);
}
