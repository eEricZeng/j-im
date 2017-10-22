/**
 * 
 */
package org.tio.im.common.packets;

/**
 * @author WChao
 *
 */
public class HeartbeatBody extends Message{
	
	private byte hbbyte;

	public byte getHbbyte() {
		return hbbyte;
	}

	public HeartbeatBody setHbbyte(byte hbbyte) {
		this.hbbyte = hbbyte;
		return this;
	}
	
}
