package com.github.javlock.games.space.client.online.game.header.window;

import org.lwjgl.opengl.GLCapabilities;

import lombok.Getter;
import lombok.Setter;

public class WindowHeader {

	@Getter
	@Setter
	static boolean leadEnabled = true;
	@Getter
	@Setter
	static boolean hUDnabled = true;

	@Getter
	@Setter
	static long window;
	@Getter
	@Setter
	static int width = 800;
	@Getter
	@Setter
	static int height = 600;
	@Getter
	@Setter
	static int fbWidth = 800;
	@Getter
	@Setter
	static int fbHeight = 600;

	@Getter
	@Setter
	static GLCapabilities caps;

}
