package org.tio.im.server.http.controller;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.im.common.http.HttpRequestPacket;
import org.tio.im.common.http.HttpResponsePacket;
import org.tio.im.common.http.UploadFile;
import org.tio.im.server.http.DefaultHttpRequestHandler.User;
import org.tio.im.server.http.HttpServerConfig;
import org.tio.im.server.http.annotation.RequestPath;
import org.tio.im.server.util.Resps;
import org.tio.json.Json;
/**
 * @author tanyaowu 
 * 2017年6月29日 下午7:53:59
 */
@RequestPath(value = "/test")
public class TestController {
	private static Logger log = LoggerFactory.getLogger(TestController.class);

	String html = "<div style='position:relation;border-radius:10px;text-align:center;padding:10px;font-size:40pt;font-weight:bold;background-color:##e4eaf4;color:#2d8cf0;border:0px solid #2d8cf0; width:600px;height:400px;margin:auto;box-shadow: 1px 1px 50px #000;position: fixed;top:0;left:0;right:0;bottom:0;'>"
			+ "<a style='text-decoration:none' href='https://git.oschina.net/tywo45/t-io' target='_blank'>"
			+ "<div style='text-shadow: 8px 8px 8px #99e;'>hello tio httpserver</div>" + "</a>" + "</div>";

	String txt = html;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public TestController() {
	}

	@RequestPath(value = "/json")
	public HttpResponsePacket json(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext channelContext)
			throws Exception {
		HttpResponsePacket ret = Resps.json(httpRequestPacket, "{\"ret\":\"OK\"}", httpServerConfig.getCharset());
		return ret;
	}

	@RequestPath(value = "/txt")
	public HttpResponsePacket txt(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext channelContext)
			throws Exception {
		HttpResponsePacket ret = Resps.txt(httpRequestPacket, txt, httpServerConfig.getCharset());
		return ret;
	}

	@RequestPath(value = "/html")
	public HttpResponsePacket html(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext channelContext)
			throws Exception {
		HttpResponsePacket ret = Resps.html(httpRequestPacket, html, httpServerConfig.getCharset());
		return ret;
	}

	@RequestPath(value = "/abtest")
	public HttpResponsePacket abtest(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext channelContext)
			throws Exception {
		HttpResponsePacket ret = Resps.html(httpRequestPacket, "OK", httpServerConfig.getCharset());
		return ret;
	}

	/**
	 * 测试映射重复
	 */
	@RequestPath(value = "/abtest")
	public HttpResponsePacket abtest1(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig, ChannelContext channelContext)
			throws Exception {
		log.info("");
		HttpResponsePacket ret = Resps.html(httpRequestPacket, "OK---------1", httpServerConfig.getCharset());
		return ret;
	}

	@RequestPath(value = "/filetest")
	public HttpResponsePacket filetest(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig,
			ChannelContext channelContext) throws Exception {
		HttpResponsePacket ret = Resps.file(httpRequestPacket, new File("d:/迷你pdf阅读器.exe"));
		return ret;
	}

	@RequestPath(value = "/filetest.zip")
	public HttpResponsePacket filetest_zip(HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig,ChannelContext channelContext) throws Exception {
		HttpResponsePacket ret = Resps.file(httpRequestPacket, new File("d:/eclipse-jee-neon-R-win32-x86_64.zip"));
		return ret;
	}

	/**
	 * 上传文件测试
	 * @param uploadFile
	 * @param httpRequestPacket
	 * @param httpServerConfig
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@RequestPath(value = "/upload")
	public HttpResponsePacket upload(UploadFile uploadFile, String before, String end, HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig,ChannelContext channelContext) throws Exception {
		HttpResponsePacket ret;
		if (uploadFile != null) {
			File file = new File("c:/" + uploadFile.getName());
			FileUtils.writeByteArrayToFile(file, uploadFile.getData());

			System.out.println("【" + before + "】");
			System.out.println("【" + end + "】");

			ret = Resps.html(httpRequestPacket, "文件【" + uploadFile.getName() + "】【" + uploadFile.getSize() + "字节】上传成功", httpServerConfig.getCharset());
		} else {
			ret = Resps.html(httpRequestPacket, "请选择文件再上传", httpServerConfig.getCharset());
		}
		return ret;
	}

	@RequestPath(value = "/post")
	public HttpResponsePacket post(String before, String end, HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig,ChannelContext channelContext) throws Exception {
		HttpResponsePacket ret = Resps.html(httpRequestPacket, "before:" + before + "<br>end:" + end, httpServerConfig.getCharset());
		return ret;
		
	}
	
	@RequestPath(value = "/plain")
	public HttpResponsePacket plain(String before, String end, HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig,ChannelContext channelContext) throws Exception {
		String bodyString = httpRequestPacket.getBodyString();
		HttpResponsePacket ret = Resps.html(httpRequestPacket, bodyString, httpServerConfig.getCharset());
		return ret;
	}
	
	@RequestPath(value = "/bean")
	public HttpResponsePacket bean(User user, HttpRequestPacket httpRequestPacket, HttpServerConfig httpServerConfig,ChannelContext channelContext) throws Exception {
		HttpResponsePacket ret = Resps.json(httpRequestPacket, Json.toFormatedJson(user), httpServerConfig.getCharset());
		return ret;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
