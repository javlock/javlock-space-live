package com.github.javlock.games.space.client.online.game.utils;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;

import org.lwjgl.glfw.GLFWWindowSizeCallback;

import com.github.javlock.games.space.GameEngine;
import com.github.javlock.games.space.client.online.game.engine.input.GameControl;
import com.github.javlock.games.space.client.online.game.header.window.WindowHeader;

public class CallbacksUtils {
	public static class ClientCallBacks {
		public static void createAllForClient() {

			GameControl.createCallbacks(WindowHeader.getWindow());

			glfwSetWindowSizeCallback(WindowHeader.getWindow(), GameEngine.wsCallback = new GLFWWindowSizeCallback() {
				@Override
				public void invoke(long window, int width, int height) {
					if (width > 0 && height > 0
							&& (WindowHeader.getWidth() != width || WindowHeader.getHeight() != height)) {
						WindowHeader.setWidth(width);
						WindowHeader.setHeight(height);
					}
				}
			});
		}
	}

}
