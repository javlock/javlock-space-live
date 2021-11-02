package com.github.javlock.games.space;

import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.github.javlock.games.space.objects.space.entity.basic.Particle;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;
import com.github.javlock.games.space.objects.space.entity.inspace.Turret;

import lombok.Getter;
import lombok.Setter;

public class GameEngine extends Thread {

	public static final String VERSION = "0.0.0.1";

	public static CopyOnWriteArrayList<Asteroid> localAsteroids = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Turret> turrets = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

	public static float maxParticleLifetime = 1.0F;

	protected static final int explosionParticles = 40;

	public static int shotOpponentMilliseconds = 20;// ПЕРЕЗАРЯДКА У ДРУГИХ
	public static Vector3f tmp3 = new Vector3f();
	public static Vector3d tmp = new Vector3d();

	protected float shotSeparation = 0.8f;
	public int shotMilliseconds = 20;// ПЕРЕЗАРЯДКА своя
	protected float maxShotLifetime = 4.0F;

	private @Getter @Setter boolean active = true;
}
