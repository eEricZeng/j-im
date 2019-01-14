package org.jim.server.command.handler.processor.group;

import org.jim.common.packets.Group;
import org.jim.common.packets.JoinGroupRespBody;
import org.jim.server.command.handler.processor.CmdProcessor;
import org.tio.core.ChannelContext;

/**
 * @author ensheng
 */
public interface GroupCmdProcessor extends CmdProcessor {
    /**
     * 加入群组处理
     * @param joinGroup
     * @param channelContext
     * @return
     */
    JoinGroupRespBody join(Group joinGroup, ChannelContext channelContext);
}
