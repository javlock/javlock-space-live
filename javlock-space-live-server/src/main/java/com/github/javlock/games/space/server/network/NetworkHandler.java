package com.github.javlock.games.space.server.network;

import java.util.concurrent.CopyOnWriteArrayList;

import com.github.javlock.games.space.server.Server;

import io.netty.channel.ChannelHandlerContext;

public class NetworkHandler {
	CopyOnWriteArrayList<ChannelHandlerContext> connections = new CopyOnWriteArrayList<>();

	private Server server;

	public NetworkHandler(Server server) {
		this.server = server;
	}

	public void sendBroadcast(Object message) {
		for (ChannelHandlerContext channelHandlerContext : connections) {
			channelHandlerContext.channel().writeAndFlush(message);
		}
	}

	public void connected(ChannelHandlerContext ctx) {
		connections.add(ctx);

	}

	public void disconnected(ChannelHandlerContext ctx) {
		connections.remove(ctx);
	}

}
