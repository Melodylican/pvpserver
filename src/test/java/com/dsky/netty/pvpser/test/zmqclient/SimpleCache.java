package com.dsky.netty.pvpser.test.zmqclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class SimpleCache<T> {

	private static final Logger logger = Logger.getLogger(SimpleCache.class);

	private static final int CACHE_QUEUE_SIZE = 100000;

	private BlockingQueue<T> queue;

	public SimpleCache() {
		this.queue = new LinkedBlockingQueue<T>(CACHE_QUEUE_SIZE);
	}

	public void put(T obj) {
		try {
			this.queue.put(obj);
		} catch (InterruptedException e) {
			logger.error("put object to cache error:" + e.getMessage());
		}
	}

	public boolean put(T obj, long timeout, TimeUnit unit) {
		boolean result = false;
		try {
			result = this.queue.offer(obj, timeout, unit);
		} catch (InterruptedException e) {
			logger.error("put object to cache timeout error:" + e.getMessage());
		}
		return result;
	}

	public T poll(long timeout, TimeUnit unit) {
		T t = null;
		try {
			t = this.queue.poll(timeout, unit);
		} catch (InterruptedException e) {
			logger.error("poll object timeout error:" + e.getMessage());
		}
		return t;
	}

	public List<T> poll(long timeout, TimeUnit unit, int size) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < size; i++) {
			T t = this.poll(timeout, unit);
			if (t != null) {
				list.add(t);
			} else {
				break;
			}
		}
		return list;
	}

	public T poll() {
		return this.queue.poll();
	}

}
