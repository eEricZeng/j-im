package org.tio.im.client;

import org.tio.client.AioClient;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Aio;
import org.tio.core.Node;
import org.tio.im.common.Const;
/**
 * 
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年8月30日 下午1:05:17
 */
public class HelloClientStarter {
	//服务器节点
	public static Node serverNode = new Node("127.0.0.1", Const.SERVER_PORT);

	//handler, 包括编码、解码、消息处理
	public static ClientAioHandler aioClientHandler = new HelloClientAioHandler();

	//事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
	public static ClientAioListener aioListener = null;

	//断链后自动连接的，不想自动连接请设为null
	private static ReconnConf reconnConf = new ReconnConf(5000L);

	//一组连接共用的上下文对象
	public static ClientGroupContext clientGroupContext = new ClientGroupContext(aioClientHandler, aioListener, reconnConf);

	public static AioClient aioClient = null;
	public static ClientChannelContext clientChannelContext = null;

	/**
	 * 启动程序入口
	 */
	public static void main(String[] args) throws Exception {
		//clientGroupContext.setHeartbeatTimeout(org.tio.examples.helloworld.common.Const.TIMEOUT);
		clientGroupContext.setHeartbeatTimeout(0);
		aioClient = new AioClient(clientGroupContext);
		clientChannelContext = aioClient.connect(serverNode);
		//连上后，发条消息玩玩
		send();
	}

	private static void send() throws Exception {
		HelloPacket packet = new HelloPacket();
		String text = "{"
						+ "\"from\":\"来源ID\","
						+ "\"to\":\"a81205df-fce7-4d90-9a5e-65f6e6b7785f\","
						+ "\"content\":\"Tcp转Ws消息来啦..!\","
						+ "\"msgType\":\"text\""
						+ "}";
		packet.setBody(text.getBytes(HelloPacket.CHARSET));
		Aio.send(clientChannelContext, packet);
	}
}
