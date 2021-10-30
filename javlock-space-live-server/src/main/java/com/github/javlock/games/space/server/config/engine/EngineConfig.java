package com.github.javlock.games.space.server.config.engine;

import lombok.Getter;
import lombok.Setter;

public class EngineConfig {
	private @Getter @Setter AsteroidConfig asteroidConfig = new AsteroidConfig();
	private @Getter @Setter ShipConfig shipConfig = new ShipConfig();

}
