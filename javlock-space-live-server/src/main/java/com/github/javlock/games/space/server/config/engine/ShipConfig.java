package com.github.javlock.games.space.server.config.engine;

import lombok.Getter;
import lombok.Setter;

public class ShipConfig {
	private @Getter @Setter int shipCount = 10;
	private @Getter @Setter int shipRespawnDelay = 100;

	private @Getter @Setter double respawnRangeXmin = -1000D;
	private @Getter @Setter double respawnRangeXmax = 1000D;
	private @Getter @Setter double respawnRangeYmin = -1000D;
	private @Getter @Setter double respawnRangeYmax = 1000D;
	private @Getter @Setter double respawnRangeZmin = -1000D;
	private @Getter @Setter double respawnRangeZmax = 1000D;

}
