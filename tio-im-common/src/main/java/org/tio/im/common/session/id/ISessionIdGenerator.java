package org.tio.im.common.session.id;

import org.tio.im.common.http.HttpConfig;

/**
 * @author wchao
 * 2017年8月15日 上午10:49:58
 */
public interface ISessionIdGenerator {

	/**
	 *
	 * @return
	 * @author wchao
	 */
	String sessionId(HttpConfig httpConfig);

}
