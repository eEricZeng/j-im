/**
 * 
 */
package org.jim.common.cluster;

/**
 * 
 * @author WChao
 *
 */
public abstract class ImCluster implements ICluster{
	
	protected ImClusterConfig clusterConfig;
	
	public ImCluster(ImClusterConfig clusterConfig){
		this.clusterConfig = clusterConfig;
	}
	public ImClusterConfig getClusterConfig() {
		return clusterConfig;
	}
}
