package org.tio.im.common;

import org.tio.core.intf.Packet;
import org.tio.im.common.packets.Command;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImPacket extends Packet
{
	private static final long serialVersionUID = 2000118564569232098L;

	protected Status status;//包状态码;
	
	protected byte[] body;//消息体;
	
	private Command command;//消息命令;
	
	private ImPacketType type;//包类型;
	
	public static byte encodeEncrypt(byte bs,boolean isEncrypt){
		if(isEncrypt){
			return (byte) (bs | Protocol.FIRST_BYTE_MASK_ENCRYPT);
		}else{
			return (byte)(Protocol.FIRST_BYTE_MASK_ENCRYPT & 0b01111111);
		}
	}
	
	public static boolean decodeCompress(byte version)
	{
		return (Protocol.FIRST_BYTE_MASK_COMPRESS & version) != 0;
	}

	public static byte encodeCompress(byte bs, boolean isCompress)
	{
		if (isCompress)
		{
			return (byte) (bs | Protocol.FIRST_BYTE_MASK_COMPRESS);
		} else
		{
			return (byte) (bs & (Protocol.FIRST_BYTE_MASK_COMPRESS ^ 0b01111111));
		}
	}

	public static boolean decodeHasSynSeq(byte firstByte)
	{
		return (Protocol.FIRST_BYTE_MASK_HAS_SYNSEQ & firstByte) != 0;
	}

	public static byte encodeHasSynSeq(byte bs, boolean hasSynSeq)
	{
		if (hasSynSeq)
		{
			return (byte) (bs | Protocol.FIRST_BYTE_MASK_HAS_SYNSEQ);
		} else
		{
			return (byte) (bs & (Protocol.FIRST_BYTE_MASK_HAS_SYNSEQ ^ 0b01111111));
		}
	}

	public static boolean decode4ByteLength(byte version)
	{
		return (Protocol.FIRST_BYTE_MASK_4_BYTE_LENGTH & version) != 0;
	}

	public static byte encode4ByteLength(byte bs, boolean is4ByteLength)
	{
		if (is4ByteLength)
		{
			return (byte) (bs | Protocol.FIRST_BYTE_MASK_4_BYTE_LENGTH);
		} else
		{
			return (byte) (bs & (Protocol.FIRST_BYTE_MASK_4_BYTE_LENGTH ^ 0b01111111));
		}
	}

	public static byte decodeVersion(byte version)
	{
		return (byte) (Protocol.FIRST_BYTE_MASK_VERSION & version);
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
		int ret = Protocol.LEAST_HEADER_LENGHT;
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
	public ImPacket(byte[] body){
		this.body = body;
	}
	public ImPacket(Command command, byte[] body)
	{
		this(body);
		this.setCommand(command);
	}
	public ImPacket(Command command, byte[] body ,ImPacketType type)
	{
		this(command,body);
		this.type = type;
	}
	public ImPacket(Command command)
	{
		this();
		this.setCommand(command);
	}

	public ImPacket()
	{

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		byte data = -127;
		System.out.println(data);
		/*int xx2x = Integer.MAX_VALUE;
		byte b = Byte.MAX_VALUE;
		short fd = Short.MAX_VALUE;
		final int fdsfd = (int) Math.pow(2, 23);*/
		/*System.out.println("Math.pow(2, 23):" + Math.pow(2, 23));
		System.out.println("Math.pow(2, 24):" + Math.pow(2, 24));
		System.out.println("Math.pow(2, 8):" + Math.pow(2, 8));
		System.out.println("Math.pow(2, 7):" + Math.pow(2, 7));
		byte xx = FIRST_BYTE_MASK_COMPRESS ^ 0b01111111;
		String xxx = Integer.toBinaryString(xx);
		System.out.println(xxx);

		byte yy = 0b01111111;
		boolean isCompress = true;
		xxx = Integer.toBinaryString(encodeCompress(yy, isCompress));
		System.out.println(xxx);

		isCompress = false;
		xxx = Integer.toBinaryString(encodeCompress(yy, isCompress));
		System.out.println(xxx);*/
	}

	public Command getCommand()
	{
		return command;
	}

	public void setCommand(Command type)
	{
		this.command = type;
	}

	/**
	 * @return the body
	 */
	public byte[] getBody()
	{
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body)
	{
		this.body = body;
	}

	/** 
	 * @see org.tio.core.intf.Packet#logstr()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月22日 下午3:15:18
	 * 
	 */
	@Override
	public String logstr()
	{
		return this.command.name();
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ImPacketType getType() {
		return type;
	}

	public void setType(ImPacketType type) {
		this.type = type;
	}
}
