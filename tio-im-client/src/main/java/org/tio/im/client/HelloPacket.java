package org.tio.im.client;

import org.tio.im.common.ImPacket;
import org.tio.im.common.packets.Command;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HelloPacket extends ImPacket
{
	private static final long serialVersionUID = 6774137219419914592L;

/**
	 * 心跳字节
	 */
	public static final byte HEARTBEAT_BYTE = -128;
	
	/**
	 * 握手字节
	 */
	public static final byte HANDSHAKE_BYTE = -127;

	/**
	 * 协议版本号
	 */
	public final static byte VERSION = 1;

	/**
	 * 消息体最多为多少
	 */
	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 2.1); //只支持多少M数据

	/**
	 * 消息头最少为多少个字节
	 */
	public static final int LEAST_HEADER_LENGHT = 4;//1+1+2 + (2+4)
	/**
	 * 加密标识位mask，1为加密，否则不加密
	 */
	public static final byte FIRST_BYTE_MASK_ENCRYPT = -128;
	/**
	 * 压缩标识位mask，1为压缩，否则不压缩
	 */
	public static final byte FIRST_BYTE_MASK_COMPRESS = 0B01000000;

	/**
	 * 是否有同步序列号标识位mask，如果有同步序列号，则消息头会带有同步序列号，否则不带
	 */
	public static final byte FIRST_BYTE_MASK_HAS_SYNSEQ = 0B00100000;

	/**
	 * 是否是用4字节来表示消息体的长度
	 */
	public static final byte FIRST_BYTE_MASK_4_BYTE_LENGTH = 0B00010000;

	/**
	 * 版本号mask
	 */
	public static final byte FIRST_BYTE_MASK_VERSION = 0B00001111;
	
	
	public static final int HEADER_LENGHT = 4;//消息头的长度
	
	public static final String CHARSET = "utf-8";
	
	public HelloPacket(){
		
	}
	public HelloPacket(Command command){
		super(command);
	}
	public HelloPacket(Command command,byte[] body){
		super(command,body);
	}
	public static byte encodeEncrypt(byte bs,boolean isEncrypt){
		if(isEncrypt){
			return (byte) (bs | FIRST_BYTE_MASK_ENCRYPT);
		}else{
			return (byte)(FIRST_BYTE_MASK_ENCRYPT & 0b01111111);
		}
	}
	public static boolean decodeCompress(byte version)
	{
		return (FIRST_BYTE_MASK_COMPRESS & version) != 0;
	}

	public static byte encodeCompress(byte bs, boolean isCompress)
	{
		if (isCompress)
		{
			return (byte) (bs | FIRST_BYTE_MASK_COMPRESS);
		} else
		{
			return (byte) (bs & (FIRST_BYTE_MASK_COMPRESS ^ 0b01111111));
		}
	}

	public static boolean decodeHasSynSeq(byte firstByte)
	{
		return (FIRST_BYTE_MASK_HAS_SYNSEQ & firstByte) != 0;
	}

	public static byte encodeHasSynSeq(byte bs, boolean hasSynSeq)
	{
		if (hasSynSeq)
		{
			return (byte) (bs | FIRST_BYTE_MASK_HAS_SYNSEQ);
		} else
		{
			return (byte) (bs & (FIRST_BYTE_MASK_HAS_SYNSEQ ^ 0b01111111));
		}
	}

	public static boolean decode4ByteLength(byte version)
	{
		return (FIRST_BYTE_MASK_4_BYTE_LENGTH & version) != 0;
	}

	public static byte encode4ByteLength(byte bs, boolean is4ByteLength)
	{
		if (is4ByteLength)
		{
			return (byte) (bs | FIRST_BYTE_MASK_4_BYTE_LENGTH);
		} else
		{
			return (byte) (bs & (FIRST_BYTE_MASK_4_BYTE_LENGTH ^ 0b01111111));
		}
	}

	public static byte decodeVersion(byte version)
	{
		return (byte) (FIRST_BYTE_MASK_VERSION & version);
	}

	/**
	 * 计算消息头占用了多少字节数
	 * @return
	 *
	 * @author: tanyaowu
	 * 2017年1月31日 下午5:32:26
	 *
	 */
	public int calcHeaderLength(boolean is4byteLength)
	{
		int ret = LEAST_HEADER_LENGHT;
		if (is4byteLength)
		{
			ret += 2;
		}
		if (this.getSynSeq() > 0)
		{
			ret += 4;
		}
		return ret;
	}
}
