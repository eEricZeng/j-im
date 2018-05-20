/**
 * 
 */
package org.jim.common.cluster;

import org.jim.common.ImPacket;
import org.tio.core.GroupContext;

/**
 * 
 * @author WChao
 *
 */
public interface ICluster {
	public void clusterToUser(GroupContext groupContext, String userid,ImPacket packet);
	public void clusterToGroup(GroupContext groupContext, String group,ImPacket packet);
	public void clusterToIp(GroupContext groupContext, String ip,ImPacket packet);
	public void clusterToChannelId(GroupContext groupContext, String channelId,ImPacket packet);
}
