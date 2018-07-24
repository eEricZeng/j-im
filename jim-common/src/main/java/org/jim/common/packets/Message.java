/**
 * 
 */
package org.jim.common.packets;

import java.io.Serializable;

import org.jim.common.utils.JsonKit;
import com.alibaba.fastjson.JSONObject;
/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年7月26日 上午11:32:57
 */
public class Message implements Serializable{
	
	private static final long serialVersionUID = -6375331164604259933L;
	protected Long createTime /*= new Date().getTime()*/;//消息创建时间;
	protected String id /*= UUIDSessionIdGenerator.instance.sessionId(null)*/;//消息id，全局唯一;
	protected Integer cmd ;//消息命令;
	protected JSONObject extras;//扩展字段;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCmd() {
		return cmd;
	}

	public void setCmd(Integer cmd) {
		this.cmd = cmd;
	}

	public JSONObject getExtras() {
		return extras;
	}

	public void setExtras(JSONObject extras) {
		this.extras = extras;
	}

	public String toJsonString() {
		return JsonKit.toJSONString(this);
	}
	
	public byte[] toByte(){
		return JsonKit.toJsonBytes(this);
	}
	
	public abstract static class Builder<T extends Message , B extends Message.Builder<T,B>>{
		
		protected Long createTime ;//消息创建时间;
		protected String id ;//消息id，全局唯一;
		protected Integer cmd ;//消息命令;
		protected JSONObject extras;//扩展字段;
		private B theBuilder = this.getThis();
		
		protected abstract B getThis();
		
		public B setCreateTime(Long createTime) {
			this.createTime = createTime;
			return theBuilder;
		}
		public B setId(String id) {
			this.id = id;
			return theBuilder;
		}
		public B setCmd(Integer cmd) {
			this.cmd = cmd;
			return theBuilder;
		}
		public B addExtra(String key , Object value) {
			 if (null == value) {
	                return theBuilder;
	         } else {
               if (null == extras) {
                   this.extras = new JSONObject();
               }
               this.extras.put(key, value);
               return theBuilder;
	         }
		}
		public abstract T build();
	}
}
