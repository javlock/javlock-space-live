package com.github.javlock.games.space.server.network.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.network.packets.PingPacket;
import com.github.javlock.games.space.network.packets.ReadyPacket;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;
import com.github.javlock.games.space.server.Server;
import com.github.javlock.games.space.server.engine.ServerEngine;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ObjectHandlerServer extends ChannelDuplexHandler {
	Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
	private Server server;

	boolean debug = true;

	public ObjectHandlerServer(Server server) {
		this.server = server;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		server.getNetworkHandler().connected(ctx);
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		server.getNetworkHandler().disconnected(ctx);
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
		if (message instanceof Shot) {
			Shot shot = (Shot) message;
			ServerEngine.directShots.add(shot);
			server.getNetworkHandler().sendBroadcast(shot);
			/*
			 * Map<Long, IoSession> targetSessions =
			 * session.getService().getManagedSessions(); for (Map.Entry<Long, IoSession>
			 * entry : targetSessions.entrySet()) { Long key = entry.getKey(); if
			 * (session.getId() != key) { IoSession targetSession = entry.getValue();
			 * targetSession.write(message); } }
			 */
			return;
		} else if (message instanceof ReadyPacket) {
			new Thread(() -> {
				for (Ship ship : ServerEngine.localShips) {
					ctx.channel().writeAndFlush(ship);
				}
				for (Asteroid asteroid : ServerEngine.localAsteroids) {
					ctx.channel().writeAndFlush(asteroid);
				}
			}).start();

		} else if (message instanceof PingPacket) {

			return;
		}

		logger.info("OBJECT:{}, TYPE:{}", message, message.getClass());

	}

}