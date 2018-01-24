/*package org.tio.im.server.command.handler;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.utils.page.Page;
import org.tio.im.common.ImPacket;
import org.tio.im.common.ImSessionContext;
import org.tio.im.common.packets.ClientPageReqBody;
import org.tio.im.common.packets.ClientPageRespBody;
import org.tio.im.common.packets.Command;
import org.tio.im.server.command.ImBsHandlerIntf;

public class ClientPageReqHandler implements ImBsHandlerIntf {
	@Override
	public Object handler(ImPacket packet, ChannelContext channelContext) throws Exception {
		if (packet.getBody() == null) {
			Aio.remove(channelContext, "body is null");
			return null;
		}

		GroupContext groupContext = channelContext.getGroupContext();
//		ImSessionContext imSessionContext = channelContext.getSessionContext();

		ClientPageReqBody clientPageReqBody = ClientPageReqBody.parseFrom(packet.getBody());
		int pageIndex = clientPageReqBody.getPageIndex();
		int pageSize = clientPageReqBody.getPageSize();
		String group = clientPageReqBody.getGroup();

		Page<ChannelContext> page = null;

		if (StringUtils.isNotBlank(group)) {
			page = Aio.getPageOfGroup(groupContext, group, pageIndex, pageSize);
		} else {
			page = Aio.getPageOfConnecteds(groupContext, pageIndex, pageSize);
		}

		ClientPageRespBody.Builder clientPageRespBodyBuilder = ClientPageRespBody.newBuilder();
		clientPageRespBodyBuilder.setPageIndex(page.getPageIndex()).setPageSize(page.getPageSize()).setRecordCount(page.getRecordCount());

		List<ChannelContext> pageData = page.getPageData();
		if (pageData != null) {
			for (ChannelContext ele : pageData) {
				clientPageRespBodyBuilder.addClients(ele.getSessionContext().getClient());
			}
		}

		ClientPageRespBody clientPageRespBody = clientPageRespBodyBuilder.build();
		ImPacket respPacket = new ImPacket(Command.COMMAND_CLIENT_PAGE_RESP, clientPageRespBody.toByteArray());
		Aio.send(channelContext, respPacket);
		Aio.send(channelContext, respPacket);
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_CLIENT_PAGE_REQ;
	}

}
*/