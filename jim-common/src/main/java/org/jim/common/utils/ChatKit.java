/**
 * 
 */
package org.jim.common.utils;

import org.apache.log4j.Logger;
import org.jim.common.Const;
import org.jim.common.ImAio;
import org.jim.common.ImConfig;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.ImStatus;
import org.jim.common.http.HttpConst;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.core.ChannelContext;
import org.tio.utils.lock.SetWithLock;
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
		    chatReqBody = JsonKit.toBean(text,ChatBody.class);
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
    	 boolean isStore = Const.ON.equals(ImConfig.isStore);
		 if(isStore){
			return ImConfig.getMessageHelper().isOnline(userid);
		 }
    	 SetWithLock<ChannelContext> toChannleContexts = ImAio.getChannelContextsByUserid(userid);
    	 if(toChannleContexts != null && toChannleContexts.size() > 0){
    		 return true;
    	 }
    	 return false;
     }
     /**
      * 获取双方会话ID(算法,from与to相与的值通过MD5加密得出)
      * @param from
      * @param to
      * @return
      */
     public static String sessionId(String from , String to){
    	 String sessionId = "";
    	 try{
	    	 byte[] fbytes = from.getBytes(Const.CHARSET);
	    	 byte[] tbytes = to.getBytes(Const.CHARSET);
	    	 boolean isfmax = fbytes.length > tbytes.length;
	    	 boolean isequal = fbytes.length == tbytes.length;
	    	 if(isfmax){
	    		 for(int i = 0 ; i < fbytes.length ; i++){
		    		 for(int j = 0 ; j < tbytes.length ; j++){
		    			 fbytes[i] = (byte) (fbytes[i]^tbytes[j]);
		    		 }
		    	 }
	    		 sessionId = new String(fbytes);
	    	 }else if(isequal){
	    		 for(int i = 0 ; i < fbytes.length ; i++){
		    		  fbytes[i] = (byte) (fbytes[i]^tbytes[i]);
		    	 }
	    		 sessionId = new String(fbytes);
	    	 }else{
	    		 for(int i = 0 ; i < tbytes.length ; i++){
		    		 for(int j = 0 ; j < fbytes.length ; j++){
		    			 tbytes[i] = (byte) (tbytes[i]^fbytes[j]);
		    		 }
		    	 }
	    		 sessionId = new String(tbytes);
	    	 }
    	 }catch (Exception e) {
			log.error(e.toString(),e);
    	 }
    	 return Md5.getMD5(sessionId);
     }
}
