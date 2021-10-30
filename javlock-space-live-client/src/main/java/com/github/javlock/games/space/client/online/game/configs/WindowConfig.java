package com.github.javlock.games.space.client.online.game.configs;

import java.awt.Toolkit;

import lombok.Getter;
import lombok.Setter;

public class WindowConfig {
	@Getter
	@Setter
	private boolean windowed = false;
	@Getter
	@Setter
	int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	@Getter
	@Setter
	int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	@Getter
	@Setter
	float distanceDraw = 1000000;
}
