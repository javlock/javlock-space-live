package com.github.javlock.games.space.utils;

import static com.github.javlock.games.space.StaticData.asteroidMesh;
import static org.lwjgl.demo.util.IOUtils.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import com.github.javlock.games.space.objects.space.entity.basic.Particle;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;

public class ProgrammUtils {
	public static void createAll() throws IOException {
		createShipProgram();
		createShip();

		createAsteroid();

		createShotProgram();

		createParticleProgram();
	}

	private static void createAsteroid() throws IOException {
		Asteroid.asteroidPositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, Asteroid.asteroidPositionVbo);
		glBufferData(GL_ARRAY_BUFFER, asteroidMesh.positions, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		Asteroid.asteroidNormalVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, Asteroid.asteroidNormalVbo);
		glBufferData(GL_ARRAY_BUFFER, asteroidMesh.normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private static void createParticleProgram() throws IOException {
		int vshader = ProgrammUtils.createShader("org/lwjgl/demo/game/particle.vs", GL_VERTEX_SHADER);
		int fshader = ProgrammUtils.createShader("org/lwjgl/demo/game/particle.fs", GL_FRAGMENT_SHADER);
		int program = ProgrammUtils.createProgram(vshader, fshader);
		glUseProgram(program);
		Particle.particle_projUniform = glGetUniformLocation(program, "proj");
		glUseProgram(0);
		Particle.particleProgram = program;
	}

	public static int createProgram(int vshader, int fshader) {
		int program = glCreateProgram();
		glAttachShader(program, vshader);
		glAttachShader(program, fshader);
		glLinkProgram(program);
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		String programLog = glGetProgramInfoLog(program);
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		return program;
	}

	public static int createShader(String resource, int type) throws IOException {
		int shader = glCreateShader(type);
		ByteBuffer source = ioResourceToByteBuffer(resource, 1024);
		PointerBuffer strings = BufferUtils.createPointerBuffer(1);
		IntBuffer lengths = BufferUtils.createIntBuffer(1);
		strings.put(0, source);
		lengths.put(0, source.remaining());
		glShaderSource(shader, strings, lengths);
		glCompileShader(shader);
		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
		String shaderLog = glGetShaderInfoLog(shader);
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}

	public static void createShip() {
		Ship.shipPositionVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, Ship.shipPositionVbo);
		glBufferData(GL_ARRAY_BUFFER, Ship.shipMesh.positions, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		Ship.shipNormalVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, Ship.shipNormalVbo);
		glBufferData(GL_ARRAY_BUFFER, Ship.shipMesh.normals, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private static void createShipProgram() throws IOException {
		int vshader = ProgrammUtils.createShader("org/lwjgl/demo/game/ship.vs", GL_VERTEX_SHADER);
		int fshader = ProgrammUtils.createShader("org/lwjgl/demo/game/ship.fs", GL_FRAGMENT_SHADER);
		int program = ProgrammUtils.createProgram(vshader, fshader);
		glUseProgram(program);
		Ship.ship_viewUniform = glGetUniformLocation(program, "view");
		Ship.ship_projUniform = glGetUniformLocation(program, "proj");
		Ship.ship_modelUniform = glGetUniformLocation(program, "model");
		glUseProgram(0);
		Ship.shipProgram = program;
	}

	private static void createShotProgram() throws IOException {
		int vshader = ProgrammUtils.createShader("org/lwjgl/demo/game/shot.vs", GL_VERTEX_SHADER);
		int fshader = ProgrammUtils.createShader("org/lwjgl/demo/game/shot.fs", GL_FRAGMENT_SHADER);
		int program = ProgrammUtils.createProgram(vshader, fshader);
		glUseProgram(program);
		Shot.shot_projUniform = glGetUniformLocation(program, "proj");
		glUseProgram(0);
		Shot.shotProgram = program;
	}
}
