package com.github.javlock.games.space.client.online.game.engine;

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
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import com.github.javlock.games.space.Cube;
import com.github.javlock.games.space.client.online.game.engine.window.GameShaders;
import com.github.javlock.games.space.client.online.game.header.GameHeader;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;

public class DrawEngine {
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
}
