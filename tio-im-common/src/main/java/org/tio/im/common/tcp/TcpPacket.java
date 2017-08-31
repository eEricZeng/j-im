/**
 * 
 */
package org.tio.im.common.tcp;

import org.tio.im.common.ImPacket;
import org.tio.im.common.ImPacketType;
import org.tio.im.common.packets.Command;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月21日 下午3:51:05
 */
public class TcpPacket extends ImPacket{
	
	private static final long serialVersionUID = -4283971967100935982L;
	
	private byte version = 0;
	private byte mask = 0;
	
	public TcpPacket(Command command, byte[] body){
		super(command, body);
	}
	public TcpPacket(Command command, byte[] body,ImPacketType type){
		super(command, body,type);
	}
	public byte getVersion() {
		return version;
	}
	public void setVersion(byte version) {
		this.version = version;
	}
	public byte getMask() {
		return mask;
	}
	public void setMask(byte mask) {
		this.mask = mask;
	}
	
}
