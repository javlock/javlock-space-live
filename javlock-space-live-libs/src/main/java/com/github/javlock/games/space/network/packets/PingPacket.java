package com.github.javlock.games.space.network.packets;

import lombok.Getter;
import lombok.Setter;

public class PingPacket extends Packet {
	/**
	 *
	 */
	private static final long serialVersionUID = -1192376821385107846L;
	@Getter
	@Setter
	boolean pong;
}
