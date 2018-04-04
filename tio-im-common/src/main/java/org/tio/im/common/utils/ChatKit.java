/**
 * 
 */
package org.tio.im.common.utils;

import org.apache.log4j.Logger;
import org.tio.core.ChannelContext;
import org.tio.im.common.ImAio;
import org.tio.im.common.ImConfig;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.ImStatus;
import org.tio.im.common.http.HttpConst;
import org.tio.im.common.packets.ChatBody;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.RespBody;
import org.tio.im.common.packets.User;
import org.tio.im.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.utils.lock.SetWithLock;
import com.alibaba.fastjson.JSONObject;

/**
 * @author WChao
 *
 */
public class ChatKit {
	
	private static Logger log = Logger.getLogger(ChatKit.class);
	/**
	 * 转换为聊天消息结构;
	 * @param body
	 * @param channelContext
	 * @return
	 */
	public static ChatBody toChatBody(byte[] body,ChannelContext channelContext){
		ChatBody chatReqBody = parseChatBody(body);
		if(chatReqBody != null){
			if(chatReqBody.getFrom() == null || "".equals(chatReqBody.getFrom())){
				ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
				User user = imSessionContext.getClient().getUser();
				if(user != null){
					chatReqBody.setFrom(user.getNick());
				}else{
					chatReqBody.setFrom(channelContext.getId());
				}
			}
		}
		return chatReqBody;
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	private static ChatBody parseChatBody(byte[] body){
		if(body == null)
			return null;
		ChatBody chatReqBody = null;
		try{
			String text = new String(body,HttpConst.CHARSET_NAME);
		    chatReqBody = JSONObject.parseObject(text,ChatBody.class);
			if(chatReqBody != null){
				if(chatReqBody.getCreateTime() == null || "".equals(chatReqBody.getCreateTime()))
					chatReqBody.setCreateTime(System.currentTimeMillis());
				chatReqBody.setId(UUIDSessionIdGenerator.instance.sessionId(null));
				return chatReqBody;
			}
		}catch(Exception e){
			
		}
		return chatReqBody;
	}
	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param packet
	 * @return
	 */
	public static ChatBody parseChatBody(String bodyStr){
		if(bodyStr == null)
			return null;
		try {
			return parseChatBody(bodyStr.getBytes(HttpConst.CHARSET_NAME));
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
  /**
   * 聊天数据格式不正确响应包
   * @param chatBody
   * @param channelContext
   * @return
   * @throws Exception
   */
   public static ImPacket  dataInCorrectRespPacket(ChannelContext channelContext) throws Exception{
	   RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C10002);
	   ImPacket respPacket = ImKit.ConvertRespPacket(chatDataInCorrectRespPacket, channelContext);
	   respPacket.setStatus(ImStatus.C10002);
	   return respPacket;
   }
   /**
    * 聊天发送成功响应包
    * @param chatBody
    * @param channelContext
    * @return
    * @throws Exception
    */
    public static ImPacket  sendSuccessRespPacket(ChannelContext channelContext) throws Exception{
 	   RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C10000);
 	   ImPacket respPacket = ImKit.ConvertRespPacket(chatDataInCorrectRespPacket, channelContext);
 	   respPacket.setStatus(ImStatus.C10000);
 	   return respPacket;
    }
    /**
     * 聊天用户不在线响应包
     * @param chatBody
     * @param channelContext
     * @return
     * @throws Exception
     */
     public static ImPacket  offlineRespPacket(ChannelContext channelContext) throws Exception{
  	   RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C10001);
  	   ImPacket respPacket = ImKit.ConvertRespPacket(chatDataInCorrectRespPacket, channelContext);
  	   respPacket.setStatus(ImStatus.C10001);
  	   return respPacket;
     }
     /**
      * 判断用户是否在线;
      * @param userid
      * @return
      */
     public static boolean isOnline(String userid){
    	 if(ImConfig.groupContext == null)
    		 return false;
    	 SetWithLock<ChannelContext> toChannleContexts = ImAio.getChannelContextsByUserid(ImConfig.groupContext,userid);
    	 if(toChannleContexts != null && toChannleContexts.size() > 0){
    		 return true;
    	 }
    	 return false;
     }
}
