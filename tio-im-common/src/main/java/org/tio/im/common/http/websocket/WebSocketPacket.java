package org.tio.im.common.http.websocket;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.tio.im.common.ImPacket;
/**
 * 
 * @author tanyaowu 
 *
 */
public class WebSocketPacket extends ImPacket {


	private static final long serialVersionUID = 5311893484391952050L;
	/**
	 * 消息体最多为多少
	 */
	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 2.1); //只支持多少M数据
	/**
	 * 是否是握手包
	 */
	private boolean isHandShake = false;


	public WebSocketPacket(byte[] body) {
		this();
		this.body = body;
	}

	public WebSocketPacket() {

	}

	public enum Opcode {

		TEXT((byte) 1), BINARY((byte) 2), CLOSE((byte) 8), PING((byte) 9), PONG((byte) 10);

		private final byte code;

		public byte getCode()
		{
			return code;
		}

		private Opcode(byte code)
		{
			this.code = code;
		}

		private static final Map<Byte, Opcode> map = new HashMap<>();
		static
		{
			for (Opcode command : values())
			{
				map.put(command.getCode(), command);
			}
		}

		public static Opcode valueOf(byte code)
		{
			return map.get(code);
		}

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {}


	/** 
	 * @see org.tio.core.intf.Packet#logstr()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2017年2月22日 下午3:15:18
	 * 
	 */
	@Override
	public String logstr() {
		try {
			if(body == null){
				return null;
			}else{
				return new String(body,CHARSET_NAME);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the isHandShake
	 */
	public boolean isHandShake() {
		return isHandShake;
	}

	/**
	 * @param isHandShake the isHandShake to set
	 */
	public void setHandShake(boolean isHandShake) {
		this.isHandShake = isHandShake;
	}
	

	public static final int MINIMUM_HEADER_LENGTH = 2;

	public static final int MAX_BODY_LENGTH = 1024 * 512;  //最多接受的1024 * 512(半M)数据

	public static final String CHARSET_NAME = "utf-8";



	private boolean wsEof;
	private Opcode wsOpcode = Opcode.BINARY;
	private boolean wsHasMask;
	private long wsBodyLength;
	private byte[] wsMask;
	private String wsBodyText;  //当为文本时才有此字段
	
	/**
	 * @return the wsEof
	 */
	public boolean isWsEof()
	{
		return wsEof;
	}

	/**
	 * @param wsEof the wsEof to set
	 */
	public void setWsEof(boolean wsEof)
	{
		this.wsEof = wsEof;
	}

	/**
	 * @return the wsOpcode
	 */
	public Opcode getWsOpcode()
	{
		return wsOpcode;
	}

	/**
	 * @param wsOpcode the wsOpcode to set
	 */
	public void setWsOpcode(Opcode wsOpcode)
	{
		this.wsOpcode = wsOpcode;
	}

	/**
	 * @return the wsHasMask
	 */
	public boolean isWsHasMask()
	{
		return wsHasMask;
	}

	/**
	 * @param wsHasMask the wsHasMask to set
	 */
	public void setWsHasMask(boolean wsHasMask)
	{
		this.wsHasMask = wsHasMask;
	}

	/**
	 * @return the wsBodyLength
	 */
	public long getWsBodyLength()
	{
		return wsBodyLength;
	}

	/**
	 * @param wsBodyLength the wsBodyLength to set
	 */
	public void setWsBodyLength(long wsBodyLength)
	{
		this.wsBodyLength = wsBodyLength;
	}

	/**
	 * @return the wsMask
	 */
	public byte[] getWsMask()
	{
		return wsMask;
	}

	/**
	 * @param wsMask the wsMask to set
	 */
	public void setWsMask(byte[] wsMask)
	{
		this.wsMask = wsMask;
	}



	/**
	 * @return the wsBodyText
	 */
	public String getWsBodyText()
	{
		return wsBodyText;
	}

	/**
	 * @param wsBodyText the wsBodyText to set
	 */
	public void setWsBodyText(String wsBodyText)
	{
		this.wsBodyText = wsBodyText;
	}

//	/**
//	 * @return the headers
//	 */
//	public Map<String, String> getHeaders() {
//		return headers;
//	}
//
//	/**
//	 * @param headers the headers to set
//	 */
//	public void setHeaders(Map<String, String> headers) {
//		this.headers = headers;
//	}
}
