package com.github.javlock.games.space.client.online.game.engine;

import static com.github.javlock.games.space.StaticData.asteroidMesh;
import static com.github.javlock.games.space.StaticData.broadphase;
import static com.github.javlock.games.space.StaticData.narrowphase;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.createFullScreenQuad;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.frustumIntersection;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.matrixBuffer;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.projMatrix;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.viewMatrix;
import static org.lwjgl.demo.util.IOUtils.ioResourceToByteBuffer;
import static org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.nglfwGetFramebufferSize;
import static org.lwjgl.opengl.ARBSeamlessCubeMap.GL_TEXTURE_CUBE_MAP_SEAMLESS;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.NoSuchAlgorithmException;

import org.joml.GeometryUtils;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.Cube;
import com.github.javlock.games.space.GameEngine;
import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.client.online.game.engine.input.GameControl;
import com.github.javlock.games.space.client.online.game.header.GameHeader;
import com.github.javlock.games.space.client.online.game.header.control.MouseHeader;
import com.github.javlock.games.space.client.online.game.header.window.WindowHeader;
import com.github.javlock.games.space.client.online.game.network.handler.ObjectHandlerClient;
import com.github.javlock.games.space.client.online.game.objects.space.entity.SpaceCamera;
import com.github.javlock.games.space.client.online.game.utils.GameUtils;
import com.github.javlock.games.space.network.packets.Packet;
import com.github.javlock.games.space.network.packets.PingPacket;
import com.github.javlock.games.space.network.packets.ReadyPacket;
import com.github.javlock.games.space.objects.space.entity.basic.Particle;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;
import com.github.javlock.games.space.utils.ProgrammUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientGameEngine extends GameEngine {

	private ChannelFuture future;

	Logger logger = LoggerFactory.getLogger("ClientGameEngine");

	private boolean windowed = false;

	private long lastShotTime = 0L;
	private long lastTime = System.nanoTime();

	// НУЖЕН ДЛЯ ОПРЕДЕЛЕНИЯ СНАРЯДОВ
	private Vector3d newPosition = new Vector3d();

	// WTF
	private Vector3f tmp4 = new Vector3f();

	private Matrix4f viewProjMatrix = new Matrix4f();
	private Matrix4f invViewMatrix = new Matrix4f();
	private Matrix4f invViewProjMatrix = new Matrix4f();

	private GLCapabilities caps;
	private GLFWWindowSizeCallback wsCallback;
	private Callback debugProc;

	boolean firstShot = false;

	private Bootstrap bootstrap;

	private NioEventLoopGroup nioEventLoopGroup;

	// ТЕКСТУРА
	private void createCubemapTexture() throws IOException {
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, tex);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		ByteBuffer imageBuffer;
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		String[] names = { "right", "left", "top", "bottom", "front", "back" };
		ByteBuffer image;
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_GENERATE_MIPMAP, GL_TRUE);
		for (int i = 0; i < 6; i++) {
			imageBuffer = ioResourceToByteBuffer("org/lwjgl/demo/space_" + names[i] + (i + 1) + ".jpg", 8 * 1024);
			if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
				throw new IOException("Failed to read image information: " + stbi_failure_reason());
			}
			image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			if (image == null) {
				throw new IOException("Failed to load image: " + stbi_failure_reason());
			}
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB8, w.get(0), h.get(0), 0, GL_RGB,
					GL_UNSIGNED_BYTE, image);
			stbi_image_free(image);
		}
		if (caps.OpenGL32 || caps.GL_ARB_seamless_cube_map) {
			glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		}
	}

	public void emitExplosion(Vector3d p, Vector3f normal) {
		int c = explosionParticles;
		if (normal != null) {
			GeometryUtils.perpendicular(normal, tmp4, tmp3);
		}
		for (int i = 0; i < explosionParticles; i++) {

			Particle particleDataObjectPairD = new Particle();
			Vector3d particlePosition = particleDataObjectPairD.getPosition();
			Vector4d particleVelocity = particleDataObjectPairD.getParticleVelocity();
			if (particleVelocity.w <= 0.0F) {
				if (normal != null) {
					float r1 = (float) Math.random() * 2.0F - 1.0F;
					float r2 = (float) Math.random() * 2.0F - 1.0F;
					particleVelocity.x = normal.x + r1 * tmp4.x + r2 * tmp3.x;
					particleVelocity.y = normal.y + r1 * tmp4.y + r2 * tmp3.y;
					particleVelocity.z = normal.z + r1 * tmp4.z + r2 * tmp3.z;
				} else {
					float x = (float) Math.random() * 2.0F - 1.0F;
					float y = (float) Math.random() * 2.0F - 1.0F;
					float z = (float) Math.random() * 2.0F - 1.0F;
					particleVelocity.x = x;
					particleVelocity.y = y;
					particleVelocity.z = z;
				}
				particleVelocity.normalize3();
				particleVelocity.mul(140);
				particleVelocity.w = 0.01f;
				particlePosition.set(p);
				if (c-- == 0) {
					break;
				}
			} // IF
			particles.add(particleDataObjectPairD);
		}
	}

	public void exit() throws InterruptedException {
		setActive(false);
		future.awaitUninterruptibly().syncUninterruptibly();
		nioEventLoopGroup.shutdownGracefully();
	}

	private void init() throws IOException {
		logger.info("init-start");
		showHelpInfo();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_SAMPLES, 4);

		long monitor = glfwGetPrimaryMonitor();
		GLFWVidMode vidmode = glfwGetVideoMode(monitor);
		if (!windowed) {
			WindowHeader.setWidth(vidmode.width());
			WindowHeader.setHeight(vidmode.height());
			WindowHeader.setFbWidth(WindowHeader.getWidth());
			WindowHeader.setFbHeight(WindowHeader.getHeight());
		}

		String title = GameHeader.title + " " + GameEngine.VERSION;
		WindowHeader.setWindow(glfwCreateWindow(WindowHeader.getWidth(), WindowHeader.getHeight(), title,
				!windowed ? monitor : 0L, NULL));

		if (WindowHeader.getWindow() == NULL) {
			throw new AssertionError("Failed to create the GLFW window");
		}
		glfwSetCursor(WindowHeader.getWindow(), glfwCreateStandardCursor(GLFW_CROSSHAIR_CURSOR));

		// TODO CREATE CALLBACKS
		GameControl.createCallbacks(WindowHeader.getWindow());

		glfwSetWindowSizeCallback(WindowHeader.getWindow(), wsCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				if (width > 0 && height > 0
						&& (WindowHeader.getWidth() != width || WindowHeader.getHeight() != height)) {
					WindowHeader.setWidth(width);
					WindowHeader.setHeight(height);
				}
			}
		});

		glfwMakeContextCurrent(WindowHeader.getWindow());// СОЗДАНИЕ КОНТЕКСТА
		glfwSwapInterval(0);
		glfwShowWindow(WindowHeader.getWindow());// ПОКАЗАТЬ ОКНО

		IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
		nglfwGetFramebufferSize(WindowHeader.getWindow(), memAddress(framebufferSize), memAddress(framebufferSize) + 4);
		WindowHeader.setFbWidth(framebufferSize.get(0));
		WindowHeader.setFbHeight(framebufferSize.get(1));
		caps = GL.createCapabilities();
		if (!caps.OpenGL20) {
			throw new AssertionError("Requires OpenGL 2.0.");
		}
		debugProc = GLUtil.setupDebugMessageCallback();

		/* Create all needed GL resources */

		ProgrammUtils.createAll();

		createCubemapTexture();

		createFullScreenQuad();

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		logger.info("init-end");
	}

	public void initConfig() throws IOException, NoSuchAlgorithmException {
		GameUtils.initConfig();
	}

	public void initData() throws IOException {
		StaticData.init();
	}

	private void loop() {
		logger.info("loop-start");
		while (!glfwWindowShouldClose(WindowHeader.getWindow())) {
			glfwPollEvents();
			glViewport(0, 0, WindowHeader.getFbWidth(), WindowHeader.getFbHeight());
			update();
			render();
			glfwSwapBuffers(WindowHeader.getWindow());
		}
		logger.info("loop-end");
	}

	private void render() {
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
		DrawEngine.drawAll();
	}

	public void send(Object object) {
		if (future != null) {
			future.channel().writeAndFlush(object);
		}
	}

	private void shoot() {
		try {

			Shot shot = new Shot();
			Vector3d shotPosition = shot.getPosition();
			Vector4f shotVel = shot.getProjectileVelocity();

			//
			Vector3f tmp2 = new Vector3f();
			//

			//
			invViewProjMatrix.transformProject(tmp2.set(MouseHeader.getMouseX(), -MouseHeader.getMouseY(), 1.0F))
					.normalize();

			SpaceCamera camera = GameHeader.camera;

			if (shotVel.w <= 0.0F) {
				shotVel.x = camera.linearVel.x + tmp2.x * Shot.shotVelocity;
				shotVel.y = camera.linearVel.y + tmp2.y * Shot.shotVelocity;
				shotVel.z = camera.linearVel.z + tmp2.z * Shot.shotVelocity;
				shotVel.w = 0.01f;
				if (!firstShot) {
					shotPosition.set(camera.right(tmp3)).mul(shotSeparation).add(camera.position);
					firstShot = true;
				} else {
					shotPosition.set(camera.right(tmp3)).mul(-shotSeparation).add(camera.position);
					firstShot = false;//
				}
			}
			//
			directShots.add(shot);
			send(shot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showHelpInfo() {
		logger.info("showHelpInfo-start");
		// ad ws qe lr

		logger.info("showHelpInfo-end");
	}

	public void startGame() {
		logger.info("startGame-start");

		try {

			init();
			loop();
			if (debugProc != null) {
				debugProc.free();
			}
			GameHeader.getKeyCallback().free();
			GameHeader.getControlHeader().getMouseHeader().getCpCallback().free();
			MouseHeader.getMbCallback().free();
			GameHeader.getFbCallback().free();
			wsCallback.free();
			glfwDestroyWindow(WindowHeader.getWindow());
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			glfwTerminate();
		}
		logger.info("startGame-end");
	}

	public void startNetwork() {
		for (;;) {
			try {
				bootstrap = new Bootstrap();
				nioEventLoopGroup = new NioEventLoopGroup();
				bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class)

						.handler(new ChannelInitializer<SocketChannel>() {
							@Override
							protected void initChannel(SocketChannel ch) throws Exception {
								try {
									ChannelPipeline p = ch.pipeline();

									// objects
									p.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers
											.softCachingConcurrentResolver(Packet.class.getClassLoader())));
									p.addLast(new ObjectEncoder());
									// p.addLast(new JSONRecHandler());
									// core
									p.addLast(new ObjectHandlerClient());

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
				//

				String host = GameHeader.getClientConfig().getNetworkConfig().getServerHost();
				int port = GameHeader.getClientConfig().getNetworkConfig().getServerPort();

				future = bootstrap.connect(host, port).awaitUninterruptibly();
				boolean result = future.isSuccess();
				if (result) {
					send(new ReadyPacket());
					break;
				}
			} catch (Exception e) {
			}
		}
		new Thread(() -> {
			do {
				try {
					send(new PingPacket());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

			} while (isActive());
		}).start();
	}

	private void update() {
		long thisTime = System.nanoTime();
		float deltaTime = (thisTime - lastTime) / 1E9f;
		lastTime = thisTime;
		updateShots(deltaTime);
		updateParticles(deltaTime);
		// ПЕРЕМЕЩЕНИЕ КАМЕРЫ НА ДИСТАНЦИЮ ИСХОДЯ ИЗ deltaTime
		GameHeader.camera.update(deltaTime);

		// УСТАНОВКА ПЕРСПЕКТИВЫ
		projMatrix.setPerspective((float) Math.toRadians(40.0F),
				(float) WindowHeader.getWidth() / WindowHeader.getHeight(), 0.1f,
				GameHeader.getClientConfig().getWindowConfig().getDistanceDraw());

		viewMatrix.set(GameHeader.camera.rotation).invert(invViewMatrix);
		viewProjMatrix.set(projMatrix).mul(viewMatrix).invert(invViewProjMatrix);
		frustumIntersection.set(viewProjMatrix);

		/* Update the background shader */
		glUseProgram(Cube.cubemapProgram);
		glUniformMatrix4fv(Cube.cubemap_invViewProjUniform, false, invViewProjMatrix.get(matrixBuffer));

		/* Update the ship shader */
		glUseProgram(Ship.shipProgram);
		glUniformMatrix4fv(Ship.ship_viewUniform, false, viewMatrix.get(matrixBuffer));
		glUniformMatrix4fv(Ship.ship_projUniform, false, projMatrix.get(matrixBuffer));

		/* Update the shot shader */
		glUseProgram(Shot.shotProgram);
		glUniformMatrix4fv(Shot.shot_projUniform, false, matrixBuffer);
		/* Update the particle shader */
		glUseProgram(Particle.particleProgram);
		glUniformMatrix4fv(Particle.particle_projUniform, false, matrixBuffer);

		/* управление */
		GameControl.updateControls();

		/* Выстрелы */
		if (MouseHeader.isLeftMouseDown() && (thisTime - lastShotTime >= 1E6 * shotMilliseconds)) {
			shoot();
			lastShotTime = thisTime;
		}
	}

	// ОБНОВЛЕНИЕ ЧАСТИЦ (И УДАЛЕНИЕ ПРИ .w<0 или .w > maxParticleLifetime)
	private void updateParticles(float deltaTime) {

		for (Particle particle : particles) {

			Vector4d particleVelocity = particle.getParticleVelocity();

			// TODO ЗАЧЕМ ЭТО
			if (particleVelocity.w <= 0.0F) {
				continue;
			}

			particleVelocity.w += deltaTime;
			Vector3d particlePosition = particle.getPosition();

			double x = particleVelocity.x;
			double y = particleVelocity.y;
			double z = particleVelocity.z;
			newPosition.set(x, y, z).mul(deltaTime).add(particlePosition);

			// У частицы истекло время жизни
			if (particleVelocity.w > maxParticleLifetime) {
				particleVelocity.w = 0.0F;
				particles.remove(particle);
				continue;
			}
			particlePosition.set(newPosition);
		}
	}

	private void updateShots(float deltaTime) {

		for (Shot shot : directShots) {

			Vector4f projectileVelocity = shot.getProjectileVelocity();
			projectileVelocity.w += deltaTime;
			Vector3d projectilePosition = shot.getPosition();
			newPosition.set(projectileVelocity.x, projectileVelocity.y, projectileVelocity.z).mul(deltaTime)
					.add(projectilePosition);
			if (projectileVelocity.w > maxShotLifetime) {
				projectileVelocity.w = 0.0F;
				directShots.remove(shot);
				continue;
			}
			if (projectileVelocity.w <= 0.0F) {
				directShots.remove(shot);
				continue;
			}
			/* Test against ships */

			for (Ship ship : localShips) {
				if (ship == null) {
					continue;
				}
				double x = ship.getPosition().x;
				double y = ship.getPosition().y;
				double z = ship.getPosition().z;

				if (broadphase(x, y, z, Ship.shipMesh.boundingSphereRadius, ship.getScale(), projectilePosition,
						newPosition)
						&& narrowphase(Ship.shipMesh.positions, x, y, z, ship.getScale(), projectilePosition,
								newPosition, tmp, StaticData.tmp2)) {
					projectileVelocity.w = 0.0F;
					// continue projectiles;
				}

			}
			/* Test against asteroids */
			for (Asteroid asteroid2 : localAsteroids) {
				if (asteroid2 == null) {
					localAsteroids.remove(asteroid2);
					continue;
				}
				float scale = asteroid2.getScale();

				double x = asteroid2.getPosition().x;
				double y = asteroid2.getPosition().y;
				double z = asteroid2.getPosition().z;

				if (broadphase(x, y, z, asteroidMesh.boundingSphereRadius, scale, projectilePosition, newPosition)
						&& narrowphase(asteroidMesh.positions, x, y, z, scale, projectilePosition, newPosition, tmp,
								StaticData.tmp2) //
				) {

					projectileVelocity.w = 0.0F;
					// continue projectiles;
				}

			}
			projectilePosition.set(newPosition);
		}

	}

}
