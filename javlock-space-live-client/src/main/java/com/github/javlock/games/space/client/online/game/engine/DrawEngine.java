package com.github.javlock.games.space.client.online.game.engine;

import static com.github.javlock.games.space.StaticData.asteroidMesh;
import static com.github.javlock.games.space.StaticData.sphereMesh;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.frustumIntersection;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.matrixBuffer;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.modelMatrix;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.projMatrix;
import static com.github.javlock.games.space.client.online.game.engine.window.GameShaders.viewMatrix;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
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
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import com.github.javlock.games.space.Cube;
import com.github.javlock.games.space.GameEngine;
import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.client.online.game.engine.window.GameShaders;
import com.github.javlock.games.space.client.online.game.header.GameHeader;
import com.github.javlock.games.space.client.online.game.header.window.WindowHeader;
import com.github.javlock.games.space.client.online.game.objects.space.entity.SpaceCamera;
import com.github.javlock.games.space.objects.space.entity.basic.Particle;
import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;

public class DrawEngine {
	protected static int maxShots = 102400;

	public static final int maxParticles = 4096 * 4;
	private static FloatBuffer particleVertices = BufferUtils.createFloatBuffer(6 * 6 * maxParticles);

	protected static float particleSize = 1.0F;

	private static FloatBuffer shotsVertices = BufferUtils.createFloatBuffer(6 * 6 * maxShots);

	protected static float shotSize = 0.5f;

	private static FloatBuffer crosshairVertices = BufferUtils.createFloatBuffer(6 * 2);

	private static ByteBuffer charBuffer = BufferUtils.createByteBuffer(16 * 270);

	public static void drawAll() {
		DrawEngine.drawShips();
		DrawEngine.drawAsteroids();
		DrawEngine.drawCubemap();
		DrawEngine.drawShots();
		DrawEngine.drawParticles();
		drawHudShotDirection();

		drawHud();
		drawVelocityCompass();

		drawMenu();

	}

	public static void drawAsteroids() {
		glUseProgram(Ship.shipProgram);
		glBindBuffer(GL_ARRAY_BUFFER, Asteroid.asteroidPositionVbo);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		glEnableClientState(GL_NORMAL_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, Asteroid.asteroidNormalVbo);
		glNormalPointer(GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (Asteroid asteroid2 : ClientGameEngine.localAsteroids) {
			if (asteroid2 == null) {
				continue;
			}
			float x = (float) (asteroid2.getPosition().x - GameHeader.camera.position.x);
			float y = (float) (asteroid2.getPosition().y - GameHeader.camera.position.y);
			float z = (float) (asteroid2.getPosition().z - GameHeader.camera.position.z);
			if (GameShaders.frustumIntersection.testSphere(x, y, z, asteroid2.getScale())) {
				GameShaders.modelMatrix.translation(x, y, z);
				GameShaders.modelMatrix.scale(asteroid2.getScale());
				glUniformMatrix4fv(Ship.ship_modelUniform, false,
						GameShaders.modelMatrix.get(GameShaders.matrixBuffer));
				glDrawArrays(GL_TRIANGLES, 0, asteroidMesh.numVertices);
			}
		}
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	public static void drawCubemap() {
		glUseProgram(Cube.cubemapProgram);
		glVertexPointer(2, GL_FLOAT, 0, GameShaders.quadVertices);
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

	private static void drawHud() {
		if (WindowHeader.isHUDnabled()) {
			glUseProgram(0);
			drawHudShips();
			drawHudAsteroids();
			test();
		}
	}

	private static void drawHudAsteroids() {
		drawHudFor(GameEngine.localAsteroids);
	}

	private static void drawHudFor(CopyOnWriteArrayList<? extends SpaceEntity> list) {

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

			GameEngine.tmp3.set(StaticData.tmp2);

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
			String text = Integer.toString((int) (GameEngine.tmp3.length()));
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

	private static void drawHudShips() {
		drawHudFor(GameEngine.localShips);
	}

	private static void drawHudShotDirection() {
		if (WindowHeader.isLeadEnabled()) {
			try {
				glUseProgram(0);
				for (Ship enemyShip : GameEngine.localShips) {
					if (enemyShip == null) {
						return;
					}
					Vector3d targetOrigin = GameEngine.tmp;
					targetOrigin.set(enemyShip.getPosition().x, enemyShip.getPosition().y, enemyShip.getPosition().z);
					Vector3f interceptorDir = StaticData.intercept(GameHeader.camera.position, Shot.shotVelocity,
							targetOrigin, GameEngine.tmp3.set(GameHeader.camera.linearVel).negate(), StaticData.tmp2);

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

	private static void drawMenu() {
		// TODO Auto-generated method stub

	}

	public static void drawParticles() {
		particleVertices.clear();
		int num = 0;

		for (Particle particle : GameEngine.particles) {
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

	public static void drawShips() {
		glUseProgram(Ship.shipProgram);
		glBindBuffer(GL_ARRAY_BUFFER, Ship.shipPositionVbo);
		glVertexPointer(3, GL_FLOAT, 0, 0);
		glEnableClientState(GL_NORMAL_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, Ship.shipNormalVbo);
		glNormalPointer(GL_FLOAT, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (Ship ship : GameEngine.localShips) {
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

	public static void drawShots() {
		shotsVertices.clear();
		int num = 0;
		for (Shot shot : GameEngine.directShots) {

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

	private static void drawVelocityCompass() {
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

	private static void test() {
	}

}
