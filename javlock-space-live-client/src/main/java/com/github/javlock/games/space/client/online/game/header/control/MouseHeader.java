package com.github.javlock.games.space.client.online.game.header.control;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import lombok.Getter;
import lombok.Setter;

public class MouseHeader {
	@Getter
	@Setter
	static GLFWMouseButtonCallback mbCallback;
	@Getter
	@Setter
	static double mouseX = 0.0D;

	@Getter
	@Setter
	static double mouseY = 0.0D;
	@Getter
	@Setter
	private static boolean leftMouseDown = false;
	@Getter
	@Setter
	private static boolean rightMouseDown = false;
	@Getter
	@Setter
	private GLFWCursorPosCallback cpCallback;

}
