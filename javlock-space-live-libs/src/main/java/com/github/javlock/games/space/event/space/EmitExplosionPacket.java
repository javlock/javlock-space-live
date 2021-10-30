package com.github.javlock.games.space.event.space;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.github.javlock.games.space.network.packets.Packet;

import lombok.Getter;
import lombok.Setter;

public class EmitExplosionPacket extends Packet {
	/**
	 *
	 */
	private static final long serialVersionUID = -346988341754394160L;
	@Getter
	@Setter
	Vector3d p;
	@Getter
	@Setter
	Vector3f normal;
}
