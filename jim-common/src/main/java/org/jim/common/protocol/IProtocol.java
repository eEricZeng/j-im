/**
 * 
 */
package org.jim.common.protocol;

import org.jim.common.ImPacket;
import org.tio.core.ChannelContext;

/**
 * 判断协议接口
 * @author WChao
 *
 */
public interface IProtocol {
	/**
	 * 协议名称
	 * @return 如:http、ws、tcp等
	 */
	public  String name();

	/**
	 * 判断是否属于指定协议
	 * @param imPacket
	 * @param channelContext
	 * @return
	 * @throws Throwable
	 */
	public  boolean isProtocol(ImPacket imPacket,ChannelContext channelContext)throws Throwable;

	/**
	 * 获取该协议包转化器
	 * @return
	 */
	public  IConvertProtocolPacket converter();
}
