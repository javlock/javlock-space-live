package com.github.javlock.games.space.server.config;

import com.github.javlock.games.space.server.config.engine.EngineConfig;
import com.github.javlock.games.space.server.config.network.NetworkConfig;

import lombok.Getter;
import lombok.Setter;

public class ServerConfig {
	@Getter
	@Setter
	private boolean prepare;
	@Getter
	@Setter
	private NetworkConfig networkConfig = new NetworkConfig();
	@Getter
	@Setter
	private EngineConfig engineConfig = new EngineConfig();

}
