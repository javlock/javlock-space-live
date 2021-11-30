package com.github.javlock.games.space.client.online.game.network.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.client.online.game.engine.ClientGameEngine;
import com.github.javlock.games.space.client.online.game.header.GameHeader;
import com.github.javlock.games.space.event.space.EmitExplosionPacket;
import com.github.javlock.games.space.event.space.SpaceEntityDestroyEvent;
import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class ObjectHandlerClient extends ChannelDuplexHandler {
	Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

	boolean debug = true;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
		if (message instanceof Shot) {
			Shot shot = (Shot) message;
			ClientGameEngine.directShots.add(shot);
			return;
		} else if (message instanceof EmitExplosionPacket) {
			EmitExplosionPacket emitExplosionPacket = (EmitExplosionPacket) message;
			GameHeader.onlinegame.emitExplosion(emitExplosionPacket.getP(), emitExplosionPacket.getNormal());
			return;

		} else if (message instanceof SpaceEntityDestroyEvent) {
			SpaceEntityDestroyEvent spaceEntityDestroyEvent = (SpaceEntityDestroyEvent) message;

			SpaceEntity entity = spaceEntityDestroyEvent.getTargetEntity();
			Class<? extends SpaceEntity> classS = entity.getClass();
			if (classS == Asteroid.class) {
				ClientGameEngine.localAsteroids.remove(entity);
				return;
			} else if (classS == Ship.class) {
				ClientGameEngine.localShips.remove(entity);
				return;
			}

		} else if (message instanceof Ship) {
			ClientGameEngine.localShips.add((Ship) message);
			return;
		} else if (message instanceof Asteroid) {
			Asteroid asteroid = (Asteroid) message;
			ClientGameEngine.localAsteroids.add(asteroid);
			return;

		}

		logger.info("OBJECT:{}, TYPE:{}", message, message.getClass());

	}

}