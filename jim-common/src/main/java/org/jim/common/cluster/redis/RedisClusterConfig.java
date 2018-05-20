/**
 * 
 */
package org.jim.common.cluster.redis;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImAio;
import org.jim.common.ImPacket;
import org.jim.common.cluster.ImClusterConfig;
import org.jim.common.cluster.ImClusterVo;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.GroupContext;
import org.tio.utils.json.Json;

/**
 * 
 * @author WChao
 *
 */
public class RedisClusterConfig extends ImClusterConfig {
	
	private static Logger log = LoggerFactory.getLogger(RedisClusterConfig.class);
	
	public static final String IM_CLUSTER_TOPIC = "JIM_CLUSTER";

	private String topicSuffix;

	private String topic;

	private RedissonClient redisson;

	public RTopic<ImClusterVo> rtopic;
	
	/**
	 * 收到了多少次topic
	 */
	public static final AtomicLong RECEIVED_TOPIC_COUNT = new AtomicLong();
	
	/**
	 * J-IM内置的集群是用redis的topic来实现的，所以不同groupContext就要有一个不同的topicSuffix
	 * @param topicSuffix 不同类型的groupContext就要有一个不同的topicSuffix
	 * @param redisson
	 * @param groupContext
	 * @return
	 * @author: WChao
	 */
	public static RedisClusterConfig newInstance(String topicSuffix, RedissonClient redisson, GroupContext groupContext) {
		if (redisson == null) {
			throw new RuntimeException(RedissonClient.class.getSimpleName() + "不允许为空");
		}
		if (groupContext == null) {
			throw new RuntimeException("GroupContext不允许为空");
		}

		RedisClusterConfig me = new RedisClusterConfig(topicSuffix, redisson, groupContext);
		me.rtopic = redisson.getTopic(me.topic);
		me.rtopic.addListener(new MessageListener<ImClusterVo>() {
			@Override
			public void onMessage(String channel, ImClusterVo imClusterVo) {
				log.info("收到topic:{}, count:{}, ImClusterVo:{}", channel, RECEIVED_TOPIC_COUNT.incrementAndGet(), Json.toJson(imClusterVo));
				String clientid = imClusterVo.getClientId();
				if (StringUtils.isBlank(clientid)) {
					log.error("clientid is null");
					return;
				}
				if (Objects.equals(ImClusterVo.CLIENTID, clientid)) {
					log.info("自己发布的消息，忽略掉,{}", clientid);
					return;
				}

				ImPacket packet = imClusterVo.getPacket();
				if (packet == null) {
					log.error("packet is null");
					return;
				}
				packet.setFromCluster(true);
				
				//发送给所有
				boolean isToAll = imClusterVo.isToAll();
				if (isToAll) {
					//								for (GroupContext groupContext : me.groupContext) {
					Aio.sendToAll(groupContext, packet);
					//								}
					//return;
				}

				//发送给指定组
				String group = imClusterVo.getGroup();
				if (StringUtils.isNotBlank(group)) {
					ImAio.sendToGroup(group, packet);
					//return;
				}

				//发送给指定用户
				String userid = imClusterVo.getUserid();
				if (StringUtils.isNotBlank(userid)) {
					//								for (GroupContext groupContext : me.groupContext) {
					ImAio.sendToUser(userid, packet);
					//								}
					//return;
				}
				
				//发送给指定token
				String token = imClusterVo.getToken();
				if (StringUtils.isNotBlank(token)) {
					//								for (GroupContext groupContext : me.groupContext) {
					Aio.sendToToken(me.groupContext, token, packet);
					//								}
					//return;
				}

				//发送给指定ip
				String ip = imClusterVo.getIp();
				if (StringUtils.isNotBlank(ip)) {
					//								for (GroupContext groupContext : me.groupContext) {
					ImAio.sendToIp(me.groupContext, ip, packet);
					//								}
					//return;
				}
			}
		});
		return me;
	}
	private RedisClusterConfig(String topicSuffix, RedissonClient redisson, GroupContext groupContext) {
		this.setTopicSuffix(topicSuffix);
		this.setRedisson(redisson);
		this.groupContext = groupContext;
	}
	public String getTopicSuffix() {
		return topicSuffix;
	}

	public void setTopicSuffix(String topicSuffix) {
		this.topicSuffix = topicSuffix;
		this.topic = topicSuffix + IM_CLUSTER_TOPIC;

	}

	public String getTopic() {
		return topic;
	}
	
	public void publishAsyn(ImClusterVo imClusterVo) {
		rtopic.publishAsync(imClusterVo);
	}
	
	public void publish(ImClusterVo imClusterVo) {
		rtopic.publish(imClusterVo);
	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	public void setRedisson(RedissonClient redisson) {
		this.redisson = redisson;
	}
	@Override
	public void send(ImClusterVo imClusterVo) {
		rtopic.publish(imClusterVo);
	}
	@Override
	public void sendAsyn(ImClusterVo imClusterVo) {
		rtopic.publishAsync(imClusterVo);
	}
}
