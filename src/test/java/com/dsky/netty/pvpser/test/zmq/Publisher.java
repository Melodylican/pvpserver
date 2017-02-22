/**   
 * @文件名称: Publisher.java
 * @类路径: com.dsky.netty.pvpser.test.zmq
 * @描述: TODO
 * @作者：chris.li(李灿)
 * @时间：2017年2月22日 下午4:03:42
 * @版本：V1.0   
 */
package com.dsky.netty.pvpser.test.zmq;

import org.apache.log4j.Logger;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * @类功能说明：jzmq测试 消息发布者
 * @类修改者：
 * @修改日期：
 * @修改说明：
 * @公司名称：dsky
 * @作者：chris.li
 * @创建时间：2017年2月22日 下午4:03:42
 * @版本：V1.0
 */
public class Publisher {
	// 等待10个订阅者
	private static final int SUBSCRIBERS_EXPECTED = 10;
	// 定义一个全局的记录器
	private final static Logger log = Logger.getLogger(Publisher.class);

	public static void main(String[] args) throws InterruptedException{
		Context context = ZMQ.context(1);
		Socket publisher = context.socket(ZMQ.PUB);
		publisher.bind("tcp://*:5557");
		try {
			// zmq发送速度太快，在订阅者尚未与发布者建立联系时，已经开始了数据发布
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		publisher.send("send start......".getBytes(), 0);
		for (int i = 0; i < 10; i++) {
			publisher.send(("Hello world "+i).getBytes(), ZMQ.NOBLOCK);
		}
		publisher.send("send end......".getBytes(), 0);

		publisher.close();
		context.term();
	}

}
