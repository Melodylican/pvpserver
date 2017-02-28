/**   
 * @文件名称: PVPSerZmqClientSend.java
 * @类路径: com.dsky.netty.pvpser.test.zmqclient
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月28日 下午5:36:39
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.zmqclient;

import java.io.IOException;

import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;
import com.dsky.netty.pvpser.test.client.PVPClientSingleton;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月28日 下午5:36:39
 * @版本：V1.0
 */
public class PVPSerZmqClientSend {
	private static String SUBTREE = "/client/";
	private static String KEYVAL = "/client/A";
	private static int INTERVAL = 10;
	private static String IP = "192.168.2.44";
	private static String PORT = "5556";

	public void run() throws IOException {
		ZmqCliProxy zmqCliProx = new ZmqCliProxy(new ZmqHandlerMsg(10) {
		});
		System.out.println("test ...1");
		zmqCliProx.subtree(SUBTREE);
		zmqCliProx.connect("tcp://" + IP, PORT);
		System.out.println("test ...2");
		// Set random tuples into the distributed hash
		while (!Thread.currentThread().isInterrupted()) {
			// Set random value, check it was stored
			// String key = String.format("%s%d", KEYVAL, i);
			// String value = String.format("%d", System.currentTimeMillis());

			SocketRequest.Builder req = SocketRequest.newBuilder();
			req.setNumber(ProtocolCode.WAITTING_USER_JOIN_ROOM);
			req.setUserId("123456789");
			req.setRoomId("9999999999");
			req.setRequestMsg("{\"roomId\":154321321,\"userId\":123456,\"data\":\"sdfdsfsdffffffffffffffffffffffffffffffffffffffffffffffdsfdsvdsfdsfdsfdscvdsfdsfdsfds\",\"roomCreatetime\":1321456421,\"numbers\":3}");

			zmqCliProx.set("KTHXBAI", req.build().toByteArray(), 0);
			//System.out.println("test ...3");

		}
		zmqCliProx.destroy();
	}

	public static void main(String[] args) throws IOException {
		new PVPSerZmqClientSend().run();
	}

}
