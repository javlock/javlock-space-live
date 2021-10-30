package com.github.javlock.games.space.client.online.game.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.client.online.game.header.GameHeader;
import com.github.javlock.games.space.client.online.game.header.control.MouseHeader;
import com.github.javlock.games.space.client.online.game.header.window.WindowHeader;

public class GameControl {

	static long timeLead = System.currentTimeMillis() / 1000;
	static long timeHUD = System.currentTimeMillis() / 1000;

	static long timeFullStop = System.currentTimeMillis() / 1000;
	static boolean fullStopEnabled = false;

	public static void createCallbacks(long window) {
		GameHeader.setKeyCallback(new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_UNKNOWN) {
					return;
				}
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
					glfwSetWindowShouldClose(window, true);
				}
				if (action == GLFW_PRESS || action == GLFW_REPEAT) {
					GameHeader.getKeyDown()[key] = true;
				} else {
					GameHeader.getKeyDown()[key] = false;
				}
			}
		});

		MouseHeader.setMbCallback(new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (button == GLFW_MOUSE_BUTTON_LEFT) {
					if (action == GLFW_PRESS)
						MouseHeader.setLeftMouseDown(true);
					else if (action == GLFW_RELEASE)
						MouseHeader.setLeftMouseDown(false);
				} else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
					if (action == GLFW_PRESS)
						MouseHeader.setRightMouseDown(true);
					else if (action == GLFW_RELEASE)
						MouseHeader.setRightMouseDown(false);
				}
			}
		});

		GameHeader.getControlHeader().getMouseHeader().setCpCallback(new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				float normX = (float) ((xpos - WindowHeader.getWidth() / 2.0) / WindowHeader.getWidth() * 2.0);
				float normY = (float) ((ypos - WindowHeader.getHeight() / 2.0) / WindowHeader.getHeight() * 2.0);

				MouseHeader.setMouseX(
						Math.max(-WindowHeader.getWidth() / 2.0F, Math.min(WindowHeader.getWidth() / 2.0F, normX)));
				MouseHeader.setMouseY(
						Math.max(-WindowHeader.getHeight() / 2.0F, Math.min(WindowHeader.getHeight() / 2.0F, normY)));
			}
		});

		GameHeader.setFbCallback(new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				if (width > 0 && height > 0
						&& (WindowHeader.getFbWidth() != width || WindowHeader.getFbHeight() != height)) {
					WindowHeader.setFbWidth(width);
					WindowHeader.setFbHeight(height);
				}
			}
		});

		glfwSetCursorPosCallback(window, GameHeader.getControlHeader().getMouseHeader().getCpCallback());
		glfwSetFramebufferSizeCallback(WindowHeader.getWindow(), GameHeader.getFbCallback());
		glfwSetMouseButtonCallback(window, MouseHeader.getMbCallback());
		glfwSetKeyCallback(window, GameHeader.getKeyCallback());

	}

	public static void updateControls() {
		GameHeader.camera.linearAcc.zero();
		float rotZ = 0.0f;

		if (GameHeader.getKeyDown()[GLFW_KEY_G]) {
			GameHeader.camera.linearAcc.fma(GameHeader.getMainThrusterAccFactor() * 100,
					GameHeader.camera.forward(StaticData.tmp2));
		}

		if (GameHeader.getKeyDown()[GLFW_KEY_W]) {
			GameHeader.camera.linearAcc.fma(GameHeader.getMainThrusterAccFactor(),
					GameHeader.camera.forward(StaticData.tmp2));
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_S]) {
			GameHeader.camera.linearAcc.fma(-GameHeader.getMainThrusterAccFactor(),
					GameHeader.camera.forward(StaticData.tmp2));
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_D]) {
			GameHeader.camera.linearAcc.fma(GameHeader.getStraveThrusterAccFactor(),
					GameHeader.camera.right(StaticData.tmp2));
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_A]) {
			GameHeader.camera.linearAcc.fma(-GameHeader.getStraveThrusterAccFactor(),
					GameHeader.camera.right(StaticData.tmp2));
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_Q]) {
			rotZ = -1.0f;
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_E]) {
			rotZ = +1.0f;
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_L]) {
			long cur = System.currentTimeMillis() / 1000;
			boolean bool = ((cur - timeLead) >= 1);
			if (bool) {
				if (WindowHeader.isLeadEnabled()) {
					WindowHeader.setLeadEnabled(false);
				} else {
					WindowHeader.setLeadEnabled(true);
				}
				timeLead = cur;
			}
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_H]) {
			long cur = System.currentTimeMillis() / 1000;
			boolean bool = ((cur - timeHUD) >= 1);
			if (bool) {
				if (WindowHeader.isHUDnabled()) {
					WindowHeader.setHUDnabled(false);
				} else {
					WindowHeader.setHUDnabled(true);
				}
				timeHUD = cur;
			}
		}

		// FULL STOP
		if (GameHeader.getKeyDown()[GLFW_KEY_B]) {
			long cur = System.currentTimeMillis() / 1000;
			boolean bool = ((cur - timeFullStop) >= 1);
			if (bool) {
				if (fullStopEnabled) {
				}
				new Thread(new Runnable() {

					@Override
					public void run() {

						while (true) {
							if (!GameHeader.onlinegame.isActive()) {
								break;
							}
							float cameraLinearAccX = GameHeader.camera.linearAcc.x;
							float cameraLinearAccY = GameHeader.camera.linearAcc.y;
							float cameraLinearAccZ = GameHeader.camera.linearAcc.z;

							float cameralinearVelX = GameHeader.camera.linearVel.x;
							float cameralinearVelY = GameHeader.camera.linearVel.y;
							float cameralinearVelZ = GameHeader.camera.linearVel.z;

							if (1F > cameralinearVelX && -1F < cameralinearVelX) {
								cameralinearVelX = 0F;
							}
							if (1F > cameralinearVelY && -1F < cameralinearVelY) {
								cameralinearVelY = 0F;
							}
							if (1F > cameralinearVelZ && -1F < cameralinearVelZ) {
								cameralinearVelZ = 0F;
							}

							StringBuilder builder = new StringBuilder();
							builder.append('\n');
							builder.append("linearAcc.x" + cameraLinearAccX).append("\n");
							builder.append("linearAcc.y " + cameraLinearAccY).append("\n");
							builder.append("linearAcc.z " + cameraLinearAccZ).append("\n");

							builder.append("linearVel.x" + cameralinearVelX).append("\n");
							builder.append("linearVel.y " + cameralinearVelY).append("\n");
							builder.append("linearVel.z " + cameralinearVelZ).append("\n");

							LoggerFactory.getLogger(getClass()).info(builder.toString());

							// получить вектор обратный по направлению
							Vector3f backVect = GameHeader.camera.linearVel;

							// определить метод

							if ((cameralinearVelX + cameralinearVelY + cameralinearVelZ) == 0) {
								break;
							}

							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}

					}
				}, "fullStopThread").start();
				timeFullStop = cur;
			}
		}
		// FULL STOP

		if (GameHeader.getKeyDown()[GLFW_KEY_SPACE]) {
			GameHeader.camera.linearAcc.fma(GameHeader.getStraveThrusterAccFactor(),
					GameHeader.camera.up(StaticData.tmp2));
		}
		if (GameHeader.getKeyDown()[GLFW_KEY_LEFT_CONTROL]) {
			GameHeader.camera.linearAcc.fma(-GameHeader.getStraveThrusterAccFactor(),
					GameHeader.camera.up(StaticData.tmp2));
		}
		if (MouseHeader.isRightMouseDown()) {
			GameHeader.camera.angularAcc.set(
					2.0f * MouseHeader.getMouseY() * MouseHeader.getMouseY() * MouseHeader.getMouseY(),
					2.0f * MouseHeader.getMouseX() * MouseHeader.getMouseX() * MouseHeader.getMouseX(), rotZ);
		} else if (!MouseHeader.isRightMouseDown()) {
			GameHeader.camera.angularAcc.set(0, 0, rotZ);
		}
		double linearVelAbs = GameHeader.camera.linearVel.length();
		if (linearVelAbs > GameHeader.getMaxLinearVel()) {
			GameHeader.camera.linearVel.normalize().mul(GameHeader.getMaxLinearVel());
		}
	}
}
