package com.dsky.netty.pvpser.test.zmqclient;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class ZmqHandlerMsg extends Thread {

	private static final Logger logger = Logger.getLogger(ZmqHandlerMsg.class);
	private SimpleCache<kvmsg> MsgCache;
	private int timeout;

	public ZmqHandlerMsg() {
		timeout = 1000;
		MsgCache = new SimpleCache<kvmsg>();
		start();
	}

	public ZmqHandlerMsg(int timeout) {
		this.timeout = timeout;
		MsgCache = new SimpleCache<kvmsg>();
		start();
	}

	public int handler(kvmsg msg) {
		logger.info("has recv:" + msg.getKey());
		return 0;
	}

	public boolean put(kvmsg msg) {
		if (null == msg)
			return false;
		return MsgCache.put(msg, timeout, TimeUnit.MILLISECONDS);
	}

	public void run() {
		while (true) {
			kvmsg msg = MsgCache.poll();
			if (null != msg) {
				handler(msg);
				msg = null;
			}
		}
	}
}
