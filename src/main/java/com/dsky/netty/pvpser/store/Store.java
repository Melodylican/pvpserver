package com.dsky.netty.pvpser.store;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collection;

public interface Store {

	void remove(String sessionId);

	void add(String sessionId, ChannelHandlerContext ctx);

	Collection<ChannelHandlerContext> getClients();

    ChannelHandlerContext get(String sessionId);

	boolean checkExist(String sessionId);
}
