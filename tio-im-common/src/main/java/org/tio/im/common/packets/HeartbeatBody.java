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
	
	public HeartbeatBody(){}
	public HeartbeatBody(byte hbbyte){
		this.hbbyte = hbbyte;
	}
	public byte getHbbyte() {
		return hbbyte;
	}

	public HeartbeatBody setHbbyte(byte hbbyte) {
		this.hbbyte = hbbyte;
		return this;
	}
	
}
