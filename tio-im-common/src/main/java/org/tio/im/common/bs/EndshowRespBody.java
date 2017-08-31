/**
 * 
 */
package org.tio.im.common.bs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EndshowRespBody extends BaseRespBody
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(EndshowRespBody.class);

	public EndshowRespBody()
	{

	}

	private Integer liveshowid;

	public Integer getLiveshowid() {
		return liveshowid;
	}

	public void setLiveshowid(Integer liveshowid) {
		this.liveshowid = liveshowid;
	}
}
