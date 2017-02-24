package com.dsky.netty.pvpser.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.dsky.netty.pvpser.store.MemoryStore;
import com.dsky.netty.pvpser.store.Store;

public class SocketManager {
	private static final ScheduledExecutorService scheduledExecutorService = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);

	private static Store store = new MemoryStore();

	public static Store getDefaultStore() {
		return store;
	}

	public static ScheduledFuture<?> schedule(Runnable runnable, long delay) {
        return scheduledExecutorService.schedule(runnable, delay, TimeUnit.SECONDS);
	}
}