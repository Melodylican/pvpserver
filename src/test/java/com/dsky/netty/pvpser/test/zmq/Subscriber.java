/**   
 * @文件名称: Subscriber.java
 * @类路径: com.dsky.netty.pvpser.test.zmq
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月22日 下午4:05:34
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.zmq;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @类功能说明：jzmq测试类 消息订阅者
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月22日 下午4:05:34
 * @版本：V1.0
 */
public class Subscriber {
	private final static Logger log = Logger.getLogger(Subscriber.class);
	
	public static void main(String[] args) {
		Context context = ZMQ.context(1);
		Socket subscriber = context.socket(ZMQ.SUB);
		subscriber.connect("tcp://127.0.0.1:5557");
		subscriber.subscribe("".getBytes());
		int total = 0;
		while(true) {
			byte[] stringValue = subscriber.recv(0);
			String string = new String(stringValue);
			if(string.equals("send end.....")) {
				break;
			}
			total ++;
			System.out.println("[Received] "+total+" update. :" + string);
		}
		subscriber.close();
		context.term();
	}

}
