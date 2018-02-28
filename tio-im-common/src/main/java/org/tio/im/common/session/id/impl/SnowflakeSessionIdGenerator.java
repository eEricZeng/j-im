package org.tio.im.common.session.id.impl;

import org.tio.im.common.http.HttpConfig;
import org.tio.im.common.session.id.ISessionIdGenerator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;

/**
 * @author tanyaowu
 * 2017年8月15日 上午10:58:22
 */
public class SnowflakeSessionIdGenerator implements ISessionIdGenerator {

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	private Snowflake snowflake;

	/**
	 *
	 * @author tanyaowu
	 */
	public SnowflakeSessionIdGenerator() {
		snowflake = new Snowflake(RandomUtil.randomInt(0, 31), RandomUtil.randomInt(0, 31));
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public SnowflakeSessionIdGenerator(int workerId, int datacenterId) {
		snowflake = new Snowflake(workerId, datacenterId);
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String sessionId(HttpConfig httpConfig) {
		return snowflake.nextId() + "";
	}
}
