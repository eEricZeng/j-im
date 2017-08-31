/*package org.tio.im.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.utils.SystemTimer;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.ChatRespBody;
import org.tio.im.common.packets.ChatType;
import org.tio.im.common.packets.Client;
import org.tio.im.common.packets.Command;
import org.tio.im.common.packets.JoinGroupNotifyRespBody;
import org.tio.im.common.packets.JoinGroupReqBody;
import org.tio.im.common.packets.JoinGroupRespBody;
import org.tio.im.common.packets.JoinGroupResult;
import org.tio.im.common.utils.ImUtils;
import org.tio.im.server.command.ImBsHandlerIntf;
*//**
 * 
 * 
 * @author tanyaowu 
 *
 *//*
public class JoinReqHandler implements ImBsHandlerIntf {
	
	private static Logger log = LoggerFactory.getLogger(JoinReqHandler.class);
	
	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			throw new Exception("body is null");
		}

		
		JoinGroupReqBody reqBody = JoinGroupReqBody.parseFrom(packet.getBody());

		String group = reqBody.getGroup();
		if (StringUtils.isBlank(group)) {
			log.error("group is null,{}", channelContext);
			Aio.close(channelContext, "group is null when join group");
			return null;
		}
//		GroupContext groupContext = channelContext.getGroupContext();
		
		ImSessionContext imSessionContext = channelContext.getSessionContext();
		//HttpRequestPacket httpHandshakePacket = imSessionContext.getHttpHandshakePacket();
		Aio.bindGroup(channelContext, group);

		//回一条消息，告诉对方进群结果
		JoinGroupResult joinGroupResult = JoinGroupResult.JOIN_GROUP_RESULT_OK;
		JoinGroupRespBody joinRespBody = JoinGroupRespBody.newBuilder().setResult(joinGroupResult).setGroup(group).build();
		ImPacket respPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_RESP, joinRespBody.toByteArray());
		Aio.send(channelContext, respPacket);

		//发进房间通知  COMMAND_JOIN_GROUP_NOTIFY_RESP
		JoinGroupNotifyRespBody joinGroupNotifyRespBody = JoinGroupNotifyRespBody.newBuilder().setGroup(group).setClient(imSessionContext.getClient()).build();
		ImPacket respPacket2 = new ImPacket(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP, joinGroupNotifyRespBody.toByteArray());
		Aio.sendToGroup(channelContext.getGroupContext(), group, respPacket2);
		//		respPacket2.setBody(body);

		//额外再群发一条聊天消息，增加一些人气
		//		String imgsrc = ImgMnService.nextImg();
		//		String text = "<a alt='点击查看大图' href='"+imgsrc+"' target='_blank'>点击查看大图<br><img src='" + imgsrc + "'><br>点击查看大图</a>";

		Client currClient = imSessionContext.getClient();
		String nick = currClient.getUser().getNick();
		String region = imSessionContext.getDataBlock().getRegion()"";

		String formatedUserAgent  = ImUtils.formatUserAgent(channelContext);


//		String imgsrc = "http://images.rednet.cn/articleimage/2013/01/23/1403536948.jpg";
//		String href = "http://mp.weixin.qq.com/s/RSi8Au0n7UrlebVVYLGFGw";
//		String title = "";
		String content = "<div>";
		
		content += "<div style='color:#077d11;padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'>欢迎来自" + region + "的朋友<span style='border-radius: 5px;padding:0px 4px;margin:0px 4px; border:solid 1px #989898'>" + nick
				+ "</span>乘坐<span style='border-radius: 5px;padding:0px 4px;margin:0px 4px; border:solid 1px #989898'>" + formatedUserAgent
				+ "</span>进入群组" + group + "</div>";
		

//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>由于t-io作者最近正面回击了某些谣言，晒了t-io官网被DDos攻击的证据，损害了某些人的利益，导致OSC上一些用户对t-io抹黑谩骂的行为变本加厉！请大家不要回复他们！让他们自娱自乐^_^" + "</div>";
//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>大部分时候，他们DDos攻击的其实是nginx，目前t-io只提供了websocket服务，2M小网站防不了DDos攻击是业界共识，所以请大家把心态放好^_^" + "</div>";
//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>请文明聊天,不要输入政治、色情、犯罪等敏感词----<a href='https://git.oschina.net/tywo45/t-io' target='_blank'>t-io平台</a>在过滤掉这些词汇的同时，会收集这些词汇的来源。" + "</div>";
//		content += "<div style='color:#ee3344;padding:4px;border:1px solid #ee3344;border-radius:5px;margin:4px 0px;'>武汉地区的ip暂时被列入了黑名单，所以不能聊天，t-io这2M小站防不了DDos攻击，只能先这样处理了" + "</div>";
		
//		content += "<div style='color:#077d11;padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'>查看大图片可能会有点慢，是因为图片太大了，作者与DDos攻击者线下似乎达成默契，这几天没有检查到DDos攻击，也许就此泯恩仇了！</div>";

		content += "<div style='color:#077d11;padding:4px;border:1px solid #077d11;border-radius:5px;margin:4px 0px;'>";
		content += "<div><a href='http://mp.weixin.qq.com/s/RSi8Au0n7UrlebVVYLGFGw' target='_blank'><span style='width:150px;'>来电话了</span></a>（t-io作者开发的可以省电话费的公众号）" + "</div>";
		content += "<div><a href='http://www.dtcaas.com' target='_blank'><span style='width:150px;'>五方会谈</span></a>（t-io作者开发的高清视频会议平台）" + "</div>";
		content += "<div><a href='http://t-io.org:9292/ecosphere.html?v=45454549i' target='_blank'><span style='width:150px;'>助力t-io </span></a>（t-io生态圈支持作者一把）" + "</div>";
		content += "<div><a href='https://git.oschina.net/tywo45/t-io' target='_blank'><span style='width:150px;'>@码云t-io </span></a>（去免费下载 t-io代码，欢迎star和fork以备不时之需）" + "</div>";
		content += "</div>";
		
		

		content += "</div>";
//		String text = "<a alt='" + title + "' title='" + title + "' href='" + href + "' target='_blank'>" + "<img style='width:200px;height:100px;' src='" + imgsrc + "'>" + "</a>"
//				+ content;
		
		String text =  content;

		ChatRespBody.Builder builder = ChatRespBody.newBuilder();
		builder.setType(ChatType.CHAT_TYPE_PUBLIC);
		builder.setText(text);
		//builder.setFromClient(org.tio.examples.im.service.UserService.sysClient);
		builder.setGroup(group);
		builder.setTime(SystemTimer.currentTimeMillis());
		ChatRespBody chatRespBody = builder.build();
		ImPacket respPacket1 = new ImPacket(Command.COMMAND_CHAT_RESP, chatRespBody.toByteArray());
		Aio.send(channelContext, respPacket1);

		return null;
	}

	@Override
	public Command command() {
		// TODO Auto-generated method stub
		return Command.COMMAND_JOIN_GROUP_REQ;
	}
}
*/