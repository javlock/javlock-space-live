package com.github.javlock.games.space.client.online.game.engine;

import static com.github.javlock.games.space.StaticData.asteroidMesh;
import static com.github.javlock.games.space.StaticData.broadphase;
import static com.github.javlock.games.space.StaticData.narrowphase;
import static com.github.javlock.games.space.StaticData.sphereMesh;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.createCubemapProgram;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.createFullScreenQuad;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.drawAsteroids;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.drawCubemap;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.frustumIntersection;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.matrixBuffer;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.modelMatrix;
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
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL14.GL_GENERATE_MIPMAP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

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

import com.github.javlock.games.space.GameEngine;
import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.client.online.game.engine.input.GameControl;
import com.github.javlock.games.space.client.online.game.engine.window.GameShaders;
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
import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;
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

//	private int shotProgram;
//	private int shot_projUniform;

//	private int particleProgram;
//	private int particle_projUniform;

	private FloatBuffer shotsVertices = BufferUtils.createFloatBuffer(6 * 6 * maxShots);
	private FloatBuffer particleVertices = BufferUtils.createFloatBuffer(6 * 6 * maxParticles);
	private FloatBuffer crosshairVertices = BufferUtils.createFloatBuffer(6 * 2);

	private ByteBuffer charBuffer = BufferUtils.createByteBuffer(16 * 270);

	private long lastShotTime = 0L;
	private long lastTime = System.nanoTime();

	private Vector3d tmp = new Vector3d();
	// НУЖЕН ДЛЯ ОПРЕДЕЛЕНИЯ СНАРЯДОВ
	private Vector3d newPosition = new Vector3d();

	// WTF
	private Vector3f tmp3 = new Vector3f();
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

	private void drawHud() {
		if (WindowHeader.isHUDnabled()) {
			glUseProgram(0);
			drawHudShips();
			drawHudAsteroids();
			test();
		}
	}

	private void drawHudAsteroids() {
		drawHudFor(localAsteroids);
	}

	private void drawHudFor(CopyOnWriteArrayList<? extends SpaceEntity> list) {

		SpaceCamera camera = GameHeader.camera;
		Vector3d cameraPos = camera.position;

		for (SpaceEntity enemyShip : list) {

			if (enemyShip == null) {
				return;
			}
			Vector3d enemyShipPos = enemyShip.getPosition();

			Vector3f targetOrigin = StaticData.tmp2;

			targetOrigin.set((float) (enemyShipPos.x - cameraPos.x), (float) (enemyShipPos.y - cameraPos.y),
					(float) (enemyShipPos.z - cameraPos.z));

			tmp3.set(StaticData.tmp2);

			viewMatrix.transformPosition(targetOrigin);
			boolean backward = targetOrigin.z > 0.0F;
			if (backward) {
				continue;
			}
			projMatrix.transformProject(targetOrigin);
			if (targetOrigin.x < -1.0F) {
				targetOrigin.x = -1.0F;
			}
			if (targetOrigin.x > 1.0F) {
				targetOrigin.x = 1.0F;
			}
			if (targetOrigin.y < -1.0F) {
				targetOrigin.y = -1.0F;
			}
			if (targetOrigin.y > 1.0F) {
				targetOrigin.y = 1.0F;
			}

			float scale = enemyShip.getScale();
			if (enemyShip instanceof Asteroid) {
				scale = scale / 40;
			}

			float crosshairSize = scale / 50;
			float xs = crosshairSize * WindowHeader.getHeight() / WindowHeader.getWidth();
			float ys = crosshairSize;
			crosshairVertices.clear();
			crosshairVertices.put(targetOrigin.x - xs).put(targetOrigin.y - ys);
			crosshairVertices.put(targetOrigin.x + xs).put(targetOrigin.y - ys);
			crosshairVertices.put(targetOrigin.x + xs).put(targetOrigin.y + ys);
			crosshairVertices.put(targetOrigin.x - xs).put(targetOrigin.y + ys);
			crosshairVertices.flip();
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			glVertexPointer(2, GL_FLOAT, 0, crosshairVertices);
			glDrawArrays(GL_QUADS, 0, 4);
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			// Draw distance text of enemy

			int x = 100;
			int y = 100;
			String text = Integer.toString((int) (tmp3.length()));
			int quads = stb_easy_font_print(x, y, text, null, charBuffer);

			glVertexPointer(2, GL_FLOAT, 16, charBuffer);
			glPushMatrix();
			// Scroll
			glTranslatef(targetOrigin.x, targetOrigin.y - crosshairSize * 1.1f, 0F);
			float aspect = (float) WindowHeader.getWidth() / WindowHeader.getHeight();
			glScalef(1.0F / 500.0F, -1.0F / 500.0F * aspect, 0.0F);
			glDrawArrays(GL_QUADS, 0, quads * 4);
			glPopMatrix();
		}
	}

	private void drawHudShips() {
		drawHudFor(localShips);

	}

	private void drawHudShotDirection() {
		if (WindowHeader.isLeadEnabled()) {
			try {
				glUseProgram(0);
				for (Ship enemyShip : localShips) {
					if (enemyShip == null) {
						return;
					}
					Vector3d targetOrigin = tmp;
					targetOrigin.set(enemyShip.getPosition().x, enemyShip.getPosition().y, enemyShip.getPosition().z);
					Vector3f interceptorDir = StaticData.intercept(GameHeader.camera.position, shotVelocity,
							targetOrigin, tmp3.set(GameHeader.camera.linearVel).negate(), StaticData.tmp2);

					if (interceptorDir == null) {
						// FIXME interceptorDir==null
						return;
					}
					viewMatrix.transformDirection(interceptorDir);
					if (interceptorDir.z > 0.0) {
						return;
					}
					projMatrix.transformProject(interceptorDir);
					float crosshairSize = 0.01F;
					float xs = crosshairSize * WindowHeader.getHeight() / WindowHeader.getWidth();
					float ys = crosshairSize;
					crosshairVertices.clear();
					crosshairVertices.put(interceptorDir.x - xs).put(interceptorDir.y - ys);
					crosshairVertices.put(interceptorDir.x + xs).put(interceptorDir.y - ys);
					crosshairVertices.put(interceptorDir.x + xs).put(interceptorDir.y + ys);
					crosshairVertices.put(interceptorDir.x - xs).put(interceptorDir.y + ys);
					crosshairVertices.flip();
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
					glVertexPointer(2, GL_FLOAT, 0, crosshairVertices);
					glDrawArrays(GL_QUADS, 0, 4);
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void drawMenu() {
		// TODO Auto-generated method stub

	}

	private void drawParticles() {
		particleVertices.clear();
		int num = 0;

		for (Particle particle : particles) {
			Vector3d particlePosition = particle.getPosition();
			Vector4d particleVelocity = particle.getParticleVelocity();
			if (particleVelocity.w > 0.0F) {
				float x = (float) (particlePosition.x - GameHeader.camera.position.x);
				float y = (float) (particlePosition.y - GameHeader.camera.position.y);
				float z = (float) (particlePosition.z - GameHeader.camera.position.z);
				if (frustumIntersection.testPoint(x, y, z)) {
					float w = (float) particleVelocity.w;
					viewMatrix.transformPosition(StaticData.tmp2.set(x, y, z));

					float tmp2x = StaticData.tmp2.x;
					float tmp2y = StaticData.tmp2.y;
					float tmp2z = StaticData.tmp2.z;

					float tmp2xP = tmp2x + particleSize;
					float tmp2xM = tmp2x - particleSize;

					float tmp2yP = tmp2y + particleSize;
					float tmp2yM = tmp2y - particleSize;

					particleVertices.put(tmp2xM).put(tmp2yM).put(tmp2z).put(w).put(-1).put(-1);
					particleVertices.put(tmp2xP).put(tmp2yM).put(tmp2z).put(w).put(1).put(-1);
					particleVertices.put(tmp2xP).put(tmp2yP).put(tmp2z).put(w).put(1).put(1);
					particleVertices.put(tmp2xP).put(tmp2yP).put(tmp2z).put(w).put(1).put(1);
					particleVertices.put(tmp2xM).put(tmp2yP).put(tmp2z).put(w).put(-1).put(1);
					particleVertices.put(tmp2xM).put(tmp2yM).put(tmp2z).put(w).put(-1).put(-1);
					num++;
				}
			}
		}
		particleVertices.flip();
		if (num > 0) {
			glUseProgram(Particle.particleProgram);
			glDepthMask(false);
			glEnable(GL_BLEND);
			glVertexPointer(4, GL_FLOAT, 6 * 4, particleVertices);
			particleVertices.position(4);
			glTexCoordPointer(2, GL_FLOAT, 6 * 4, particleVertices);
			particleVertices.position(0);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, num * 6);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisable(GL_BLEND);
			glDepthMask(true);
		}
	}

	private void drawShips() {
		glUseProgram(Ship.shipProgram);
		glBindBuffer(GL_ARRAY_BUFFER, Ship.shipPositionVbo);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		glEnableClientState(GL_NORMAL_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, Ship.shipNormalVbo);
		glNormalPointer(GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (Ship ship : localShips) {
			if (ship == null) {
				continue;
			}
			float x = (float) (ship.getPosition().x - GameHeader.camera.position.x);
			float y = (float) (ship.getPosition().y - GameHeader.camera.position.y);
			float z = (float) (ship.getPosition().z - GameHeader.camera.position.z);
			if (frustumIntersection.testSphere(x, y, z, ship.getScale())) {
				modelMatrix.translation(x, y, z);
				modelMatrix.scale(ship.getScale());

				glUniformMatrix4fv(Ship.ship_modelUniform, false, modelMatrix.get(matrixBuffer));
				glDrawArrays(GL_TRIANGLES, 0, Ship.shipMesh.numVertices);
			}
		}
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	private void drawShots() {
		shotsVertices.clear();
		int num = 0;
		for (Shot shot : directShots) {

			Vector3d projectilePosition = shot.getPosition();
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			if (projectileVelocity.w > 0.0F) {
				float x = (float) (projectilePosition.x - GameHeader.camera.position.x);
				float y = (float) (projectilePosition.y - GameHeader.camera.position.y);
				float z = (float) (projectilePosition.z - GameHeader.camera.position.z);
				if (frustumIntersection.testPoint(x, y, z)) {
					float w = projectileVelocity.w;

					viewMatrix.transformPosition(StaticData.tmp2.set(x, y, z));

					float tmp2x = StaticData.tmp2.x;
					float tmp2y = StaticData.tmp2.y;
					float tmp2z = StaticData.tmp2.z;

					float tmp2xP = tmp2x + shotSize;
					float tmp2xM = tmp2x - shotSize;

					float tmp2yP = tmp2y + shotSize;
					float tmp2yM = tmp2y - shotSize;

					shotsVertices.put(tmp2xM).put(tmp2yM).put(tmp2z).put(w).put(-1).put(-1);
					shotsVertices.put(tmp2xP).put(tmp2yM).put(tmp2z).put(w).put(1).put(-1);
					shotsVertices.put(tmp2xP).put(tmp2yP).put(tmp2z).put(w).put(1).put(1);
					shotsVertices.put(tmp2xP).put(tmp2yP).put(tmp2z).put(w).put(1).put(1);
					shotsVertices.put(tmp2xM).put(tmp2yP).put(tmp2z).put(w).put(-1).put(1);
					shotsVertices.put(tmp2xM).put(tmp2yM).put(tmp2z).put(w).put(-1).put(-1);

					num++;
				}
			}
		}
		shotsVertices.flip();
		if (num > 0) {
			glUseProgram(Shot.shotProgram);
			glDepthMask(false);
			glEnable(GL_BLEND);
			glVertexPointer(4, GL_FLOAT, 6 * 4, shotsVertices);
			shotsVertices.position(4);
			glTexCoordPointer(2, GL_FLOAT, 6 * 4, shotsVertices);
			shotsVertices.position(0);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glDrawArrays(GL_TRIANGLES, 0, num * 6);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisable(GL_BLEND);
			glDepthMask(true);
		}
	}

	private void drawVelocityCompass() {
		glUseProgram(0);
		glEnable(GL_BLEND);
		glVertexPointer(3, GL_FLOAT, 0, sphereMesh.positions);
		glEnableClientState(GL_NORMAL_ARRAY);
		glNormalPointer(GL_FLOAT, 0, sphereMesh.normals);
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadMatrixf(projMatrix.get(matrixBuffer));
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		glLoadIdentity();
		glTranslatef(0, -1, -4);
		glMultMatrixf(viewMatrix.get(matrixBuffer));
		glScalef(0.3F, 0.3F, 0.3F);
		glColor4f(0.1F, 0.1F, 0.1F, 0.2F);
		glDisable(GL_DEPTH_TEST);
		glDrawArrays(GL_TRIANGLES, 0, sphereMesh.numVertices);
		glEnable(GL_DEPTH_TEST);
		glBegin(GL_LINES);
		glColor4f(1, 0, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(1, 0, 0);
		glColor4f(0, 1, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 1, 0);
		glColor4f(0, 0, 1, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, 1);
		glColor4f(1, 1, 1, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(GameHeader.camera.linearVel.x / GameHeader.getMaxLinearVel(),
				GameHeader.camera.linearVel.y / GameHeader.getMaxLinearVel(),
				GameHeader.camera.linearVel.z / GameHeader.getMaxLinearVel());
		glEnd();
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisable(GL_BLEND);
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
		WindowHeader.setWindow(glfwCreateWindow(WindowHeader.getWidth(), WindowHeader.getHeight(), GameHeader.title,
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
		createCubemapProgram();

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
		drawShips();
		drawAsteroids();
		drawCubemap();
		drawShots();
		drawParticles();

		drawHudShotDirection();
		drawHud();
		drawVelocityCompass();

		drawMenu();
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
				shotVel.x = camera.linearVel.x + tmp2.x * shotVelocity;
				shotVel.y = camera.linearVel.y + tmp2.y * shotVelocity;
				shotVel.z = camera.linearVel.z + tmp2.z * shotVelocity;
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

	private void test() {
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
		glUseProgram(GameShaders.getCubemapProgram());
		glUniformMatrix4fv(GameShaders.getCubemap_invViewProjUniform(), false, invViewProjMatrix.get(matrixBuffer));

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
