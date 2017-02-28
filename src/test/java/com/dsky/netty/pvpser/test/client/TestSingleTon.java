/**   
 * @文件名称: TestSingleTon.java
 * @类路径: com.dsky.netty.pvpser.test.client
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月27日 下午2:41:24
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.client;

import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;

/**
 * @类功能说明：
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月27日 下午2:41:24
 * @版本：V1.0
 */
public class TestSingleTon {
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws InterruptedException {
		int i=0;
		long start = System.currentTimeMillis();
		for(i=0;i<10000;i++) {
        //准备发送的消息
		SocketRequest.Builder req = SocketRequest.newBuilder();
		req.setNumber(ProtocolCode.WAITTING_USER_JOIN_ROOM);
		req.setUserId("123456789");
		req.setRoomId("9999999999");
		req.setRequestMsg("{\"roomId\":154321321,\"userId\":123456,\"data\":\"sdfdsfsdffffffffffffffffffffffffffffffffffffffffffffffdsfdsvdsfdsfdsfdscvdsfdsfdsfds\",\"roomCreatetime\":1321456421,\"numbers\":3}");

		SocketResponse rp = PVPClientSingleton.getSingleton().send(req.build());
		System.out.println(rp.getResponseMsg()+"        收到的消息 。。。。");
		}
		long end = System.currentTimeMillis();
		System.out.println((end-start)/10000);

	}

}
