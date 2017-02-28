/**   
 * @文件名称: PVPSerZmqClient.java
 * @类路径: com.dsky.netty.pvpser.test.zmqclient
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月28日 下午5:22:55
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.zmqclient;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;
import com.dsky.netty.pvpser.test.client.PVPClientSingleton;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @类功能说明：用于处理从zmq中接收消息和向zmq中发送消息
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月28日 下午5:22:55
 * @版本：V1.0
 */
public class PVPSerZmqClientRecevie {
	private  static String SUBTREE = "/client/";
	private  static String KEYVAL = "/client/A";
	private  static int INTERVAL = 10;
	private  static String IP="192.168.2.44";
	private  static String PORT="5556";
	
	private static final Logger logger = Logger.getLogger(PvpProxy.class);

	public void run() throws IOException {
		ZmqCliProxy zmqCliProx = new ZmqCliProxy(new ZmqHandlerMsg(10) {

			public int handler(kvmsg msg) {

				int size = msg.body().length;
				byte[] buf = new byte[size];
				System.arraycopy(msg.body(), 0, buf, 0, size);

				SocketResponse rp;
				try {
					if(buf.length >0) {
					//接收到的消息发送到PVPSer中	
					rp = PVPClientSingleton.getSingleton().send(SocketRequest.parseFrom(buf));
					System.out.println(rp.getResponseMsg()+"        收到的消息 。。。。");
					} else {
						System.out.println("收到消息的字节数为 0");
					}
				} catch (InvalidProtocolBufferException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				return 1;
			}
		});
		//zmqCliProx.destroy();
	}

	public static void main(String[] args) throws IOException {
		new PVPSerZmqClientRecevie().run();

	}
}