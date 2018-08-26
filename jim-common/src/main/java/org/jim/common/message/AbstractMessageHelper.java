/**
 * 
 */
package org.jim.common.message;

import org.jim.common.Const;
import org.jim.common.ImConfig;

/**
 * @author HP
 *
 */
public abstract class AbstractMessageHelper implements IMesssageHelper,Const{
	protected ImConfig imConfig;

	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
}
