package org.tio.im.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.im.common.http.UploadFile;
import com.xiaoleilu.hutool.util.ClassUtil;

/**
 * @author tanyaowu 
 * 2017年7月26日 下午6:46:11
 */
public class ClassUtils {
	private static Logger log = LoggerFactory.getLogger(ClassUtils.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public ClassUtils() {
	}
	
	public static boolean isSimpleTypeOrArray(Class<?> clazz){
		return ClassUtil.isSimpleTypeOrArray(clazz) || clazz.isAssignableFrom(UploadFile.class);
	}
	
	

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {
		log.info("");
	}
}
