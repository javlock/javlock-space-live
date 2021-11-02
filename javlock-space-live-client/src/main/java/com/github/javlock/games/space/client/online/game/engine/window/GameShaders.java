package com.github.javlock.games.space.client.online.game.engine.window;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import lombok.Getter;
import lombok.Setter;

public class GameShaders {

	public static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	public static FrustumIntersection frustumIntersection = new FrustumIntersection();
	public static Matrix4f projMatrix = new Matrix4f();// WORLD ?
	public static Matrix4f viewMatrix = new Matrix4f();// CAMERA
	public static Matrix4f modelMatrix = new Matrix4f();// WORLD ?

	public static @Getter @Setter ByteBuffer quadVertices;

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

}
