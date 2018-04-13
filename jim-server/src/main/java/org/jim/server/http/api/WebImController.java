/**
 * 
 */
package org.jim.server.http.api;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.http.HttpRequest;
import org.jim.common.http.HttpResponse;
import org.jim.server.http.annotation.RequestPath;
import org.jim.server.util.HttpResps;

import cn.hutool.core.io.FileUtil;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月27日 下午4:54:35
 */
@RequestPath(value = "/webim")
public class WebImController {
	
	public HttpResponse webim(HttpRequest request) throws Exception {
		String root = FileUtil.getAbsolutePath(request.getHttpConfig().getPageRoot());
		String path = request.getRequestLine().getPath();
		File file = new File(root + path);
		if (!file.exists() || file.isDirectory()) {
			if (StringUtils.endsWith(path, "/")) {
				path = path + "index.html";
			} else {
				path = path + "/index.html";
			}
			file = new File(root, path);
		}
		HttpResponse ret = HttpResps.file(request, file);
		return ret;
	}
}
