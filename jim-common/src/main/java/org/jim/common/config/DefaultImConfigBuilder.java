/**
 * 
 */
package org.jim.common.config;

import org.jim.common.http.HttpConfig;
import org.jim.common.ws.WsServerConfig;

/**
 * @author WChao
 *
 */
public class DefaultImConfigBuilder extends ImConfigBuilder {

	/* (non-Javadoc)
	 * @see org.jim.common.config.ImConfigBuilder#configHttp(org.jim.common.http.HttpConfig)
	 */
	@Override
	public ImConfigBuilder configHttp(HttpConfig httpConfig) {
		// TODO Auto-generated method stub
		return this;
	}

	/* (non-Javadoc)
	 * @see org.jim.common.config.ImConfigBuilder#configWs(org.jim.common.ws.WsServerConfig)
	 */
	@Override
	public ImConfigBuilder configWs(WsServerConfig wsServerConfig) {
		// TODO Auto-generated method stub
		return this;
	}

}
