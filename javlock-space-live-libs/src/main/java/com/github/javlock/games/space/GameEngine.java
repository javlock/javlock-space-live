package com.github.javlock.games.space;

import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import com.github.javlock.games.space.objects.space.entity.basic.Particle;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;
import com.github.javlock.games.space.objects.space.entity.inspace.Turret;

import lombok.Getter;
import lombok.Setter;

public class GameEngine extends Thread {
	public static final String VERSION = "0.0.0.1";

	public static final CopyOnWriteArrayList<Asteroid> localAsteroids = new CopyOnWriteArrayList<>();

	public static final CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();
	public static final CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();
	public static final CopyOnWriteArrayList<Turret> turrets = new CopyOnWriteArrayList<>();
	public static final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

	public static final float MAXPARTICLELIFETIME = 1.0F;
	protected static final int EXPLOSIONPARTICLESCOUNT = 40;

	public static Vector3f tmp3 = new Vector3f();
	public static Vector3d tmp = new Vector3d();
	public static GLFWWindowSizeCallback wsCallback;

	protected float shotSeparation = 0.8f;

	private @Getter @Setter boolean active = true;

	@Override
	public void run() {
		throw new UnsupportedOperationException("impl Engine (run)");
	}
}
