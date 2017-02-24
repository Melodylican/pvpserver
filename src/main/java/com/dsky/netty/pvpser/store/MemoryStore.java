package com.dsky.netty.pvpser.store;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public class MemoryStore implements Store {

	private static final ConcurrentHashMap<String, ChannelHandlerContext> clients = new ConcurrentHashMap<String, ChannelHandlerContext>();

	public ChannelHandlerContext get(String key) {
        if (this.checkExist(key)) {
            return clients.get(key);
        }

		return null;
	}

	public void remove(String key) {
        clients.remove(key);
	}

	public void add(String key, ChannelHandlerContext client) {
		if (key == null || client == null) {
            return;
        }

        clients.put(key, client);
	}

	public Collection<ChannelHandlerContext> getClients() {
        return clients.values();
	}

	public boolean checkExist(String key) {
        return clients.containsKey(key);
	}

}