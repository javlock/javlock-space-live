package com.github.javlock.games.space.server.config.engine;

import lombok.Getter;
import lombok.Setter;

public class AsteroidConfig {
	private @Getter @Setter int asteroidCount = 1024;

	private @Getter @Setter double respawnRangeXmin = -10000D;
	private @Getter @Setter double respawnRangeXmax = 10000D;
	private @Getter @Setter double respawnRangeYmin = -10000D;
	private @Getter @Setter double respawnRangeYmax = 10000D;
	private @Getter @Setter double respawnRangeZmin = -10000D;
	private @Getter @Setter double respawnRangeZmax = 10000D;
}
