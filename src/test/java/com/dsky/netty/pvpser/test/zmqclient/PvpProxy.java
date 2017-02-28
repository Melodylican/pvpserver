package com.dsky.netty.pvpser.test.zmqclient;
/*
 *     PvpProxy.java
 *
 */

import org.apache.log4j.Logger;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.dsky.netty.pvpser.common.ProtocolCode;
import com.dsky.netty.pvpser.model.Room;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketRequest;
import com.dsky.netty.pvpser.protocode.PVPSerProtocol.SocketResponse;
import com.dsky.netty.pvpser.test.client.PVPClientSingleton;
import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *  PvpProxy Demo
 */
public class PvpProxy {
	private  static String SUBTREE = "/client/";
	private  static String KEYVAL = "/client/A";
	private  static int INTERVAL = 10;
	private  static String IP="192.168.2.44";
	private  static String PORT="5556";
	
	private static final Logger logger = Logger.getLogger(PvpProxy.class);

	public void run() throws IOException {
		ZmqCliProxy zmqCliProx = new ZmqCliProxy(new ZmqHandlerMsg(10) {
			public int index;
			public int handler(kvmsg msg) {
				long curtime = System.currentTimeMillis();

				int size = msg.body().length;
				System.out.println(size+"===============================rec");
				byte[] buf = new byte[size];
				System.arraycopy(msg.body(), 0, buf, 0, size);
//				/*
				SocketResponse rp;
				try {
					if(buf.length >0) {
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
//				*/
				
				
				String val = new String(buf);
				//System.out.println(val.length()+" ----------------------------------"+val);
				buf = null;

				String key = msg.getKey();
				
				//数据同步完成标志
				if(key.equals("KTHXBAI"))
				{
					logger.info("KTHXBAI, seq:" + msg.getSequence());
					return 1;
				}
				
				long time_consumption = 1;
				try {
					time_consumption = curtime - Long.parseLong(val);
				} catch (NumberFormatException e) {
					System.out.println(e.getMessage());
					time_consumption = 0;
				}

				if (key.indexOf(KEYVAL + "0") == 0) {
					index = 0;
				}

				String shallkey = KEYVAL + index;
				if (shallkey.equals(key) ) {
					logger.debug(" seq:" + msg.getSequence() + " key:" + key + " " + curtime + " - " + val 
							+ "=" + time_consumption);
				} else if (key.indexOf(KEYVAL) == 0 && !shallkey.equals(key)){
					logger.debug(" err seq:" + msg.getSequence() + " key:" + key + " " + curtime + " - " + val
							+ "=" + time_consumption + " index:" + index);
				}

				if (shallkey.equals(key)) {
					index++;
				}
				
				if( msg.getSequence() %10000 == 0){
					logger.debug("seq:" + msg.getSequence() + " key:" + key + " " + curtime + " - " + val
							+ "=" + time_consumption);
				}
				return 1;
			}
		});

		zmqCliProx.subtree(SUBTREE);
		zmqCliProx.connect("tcp://"+IP, PORT);
	
		long i = 0;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}

		// Set random tuples into the distributed hash
		long start = System.currentTimeMillis();
		while (!Thread.currentThread().isInterrupted()) {
			// Set random value, check it was stored
			String key = String.format("%s%d", KEYVAL, i);
			String value = String.format("%d", System.currentTimeMillis());
			
			SocketRequest.Builder req = SocketRequest.newBuilder();
			req.setNumber(ProtocolCode.WAITTING_USER_JOIN_ROOM);
			req.setUserId("123456789");
			req.setRoomId("9999999999");
			req.setRequestMsg("{\"roomId\":154321321,\"userId\":123456,\"data\":\"sdfdsfsdffffffffffffffffffffffffffffffffffffffffffffffdsfdsvdsfdsfdsfdscvdsfdsfdsfds\",\"roomCreatetime\":1321456421,\"numbers\":3}");
/*
			SocketRequest sr = PVPSerProtocol.SocketRequest.newBuilder()
					.setNumber(ProtocolCode.SYSTEM_UPLOAD_HEARTBEAT)
					.setSequence(0).
					setRequestMsg("{\"roomId\":154321321,\"userId\":123456,\"data\":\"sdfdsfsdffffffffffffffffffffffffffffffffffffffffffffffdsfdsvdsfdsfdsfdscvdsfdsfdsfds\",\"roomCreatetime\":1321456421,\"numbers\":3}").
					build();
*/
			zmqCliProx.set(key, req.build().toByteArray(), 0);
			
			
			System.out.println(req.build().toByteArray().length+"++++++++++++++++++++++++++++++send");

			if (i % INTERVAL == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			
			if(i%10000==0)
				System.out.println(" start:" + start + " i:" + i);

			i++;
		}

		System.out.println(" start:" + start + " i:" + i);

		zmqCliProx.destroy();
	}

	public static void main(String[] args) throws IOException {
		//System.setProperty("java.library.path","/usr/local/lib");
		
		for (String s: args)
		{
			System.out.println(s);
		}
		
		System.out.println("args.length" + args.length);
		
		if(args.length == 5 ){
			PvpProxy.SUBTREE=args[0];
			PvpProxy.KEYVAL= args[1];
			PvpProxy.INTERVAL= Integer.parseInt(args[2]) ;
			PvpProxy.IP= args[3] ;
			PvpProxy.PORT= args[4] ;
		}
		
		System.out.println("Sub: " +   PvpProxy.SUBTREE );
		System.out.println("Key: " +   PvpProxy.KEYVAL );
		System.out.println("Interval: " +  PvpProxy.INTERVAL );
		System.out.println("IP: " +   PvpProxy.IP );
		System.out.println("PORT: " +  PvpProxy.PORT );
		
		new PvpProxy().run();
	}
}
