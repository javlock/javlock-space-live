package com.github.javlock.games.space.client.online.game.configs;

import com.github.javlock.games.space.players.Player;

import lombok.Getter;
import lombok.Setter;

public class ClientConfig {
	@Getter
	@Setter
	private boolean prepare;
	@Getter
	@Setter
	private WindowConfig windowConfig = new WindowConfig();
	@Getter
	@Setter
	private NetworkConfig networkConfig = new NetworkConfig();

	@Getter
	@Setter
	private Player player = new Player();
}
