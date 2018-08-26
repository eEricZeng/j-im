/**
 * 
 */
package org.jim.common.listener;

import org.jim.common.Const;
import org.jim.common.ImConfig;
/**
 * @author WChao
 * 2018/08/26
 */
public abstract class AbstractImBindListener implements ImBindListener,Const{
	
	protected ImConfig imConfig;

	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
}
