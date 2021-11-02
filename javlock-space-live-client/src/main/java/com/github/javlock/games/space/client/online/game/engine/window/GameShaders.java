package com.github.javlock.games.space.client.online.game.engine.window;

import static com.github.javlock.games.space.StaticData.asteroidMesh;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import com.github.javlock.games.space.client.online.game.engine.ClientGameEngine;
import com.github.javlock.games.space.client.online.game.header.GameHeader;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.utils.ProgrammUtils;

import lombok.Getter;
import lombok.Setter;

public class GameShaders {

	public static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	public static FrustumIntersection frustumIntersection = new FrustumIntersection();
	public static Matrix4f projMatrix = new Matrix4f();// WORLD ?
	public static Matrix4f viewMatrix = new Matrix4f();// CAMERA
	public static Matrix4f modelMatrix = new Matrix4f();// WORLD ?

	public static @Getter @Setter int cubemapProgram;
	public static @Getter @Setter int cubemap_invViewProjUniform;

	public static @Getter @Setter ByteBuffer quadVertices;

	public static void createCubemapProgram() throws IOException {
		int vshader = ProgrammUtils.createShader("org/lwjgl/demo/game/cubemap.vs", GL_VERTEX_SHADER);
		int fshader = ProgrammUtils.createShader("org/lwjgl/demo/game/cubemap.fs", GL_FRAGMENT_SHADER);
		int program = ProgrammUtils.createProgram(vshader, fshader);
		glUseProgram(program);
		int texLocation = glGetUniformLocation(program, "tex");
		glUniform1i(texLocation, 0);
		cubemap_invViewProjUniform = glGetUniformLocation(program, "invViewProj");
		glUseProgram(0);
		cubemapProgram = program;
	}

	public static void createFullScreenQuad() {
		quadVertices = BufferUtils.createByteBuffer(4 * 2 * 6);
		FloatBuffer fv = quadVertices.asFloatBuffer();
		fv.put(-1.0F).put(-1.0F);
		fv.put(1.0F).put(-1.0F);
		fv.put(1.0F).put(1.0F);
		fv.put(1.0F).put(1.0F);
		fv.put(-1.0F).put(1.0F);
		fv.put(-1.0F).put(-1.0F);
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
			if (frustumIntersection.testSphere(x, y, z, asteroid2.getScale())) {
				modelMatrix.translation(x, y, z);
				modelMatrix.scale(asteroid2.getScale());
				glUniformMatrix4fv(Ship.ship_modelUniform, false, modelMatrix.get(matrixBuffer));
				glDrawArrays(GL_TRIANGLES, 0, asteroidMesh.numVertices);
			}
		}
		glDisableClientState(GL_NORMAL_ARRAY);
	}

	public static void drawCubemap() {
		glUseProgram(GameShaders.getCubemapProgram());
		glVertexPointer(2, GL_FLOAT, 0, quadVertices);
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

}
