package com.github.javlock.games.space;

import java.util.concurrent.CopyOnWriteArrayList;

import com.github.javlock.games.space.objects.space.entity.basic.Particle;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;
import com.github.javlock.games.space.objects.space.entity.inspace.Turret;

import lombok.Getter;
import lombok.Setter;

public class GameEngine extends Thread {

	public static CopyOnWriteArrayList<Asteroid> localAsteroids = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Ship> localShips = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Shot> directShots = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Turret> turrets = new CopyOnWriteArrayList<>();
	public static CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();

	public static float maxParticleLifetime = 1.0F;
	protected static float particleSize = 1.0F;
	protected static final int explosionParticles = 40;
	public static final int maxParticles = 4096 * 4;
	public static float shotVelocity = 50.0F;
	public static int shotOpponentMilliseconds = 20;// ПЕРЕЗАРЯДКА У ДРУГИХ
	protected int maxShots = 102400;
	protected float shotSeparation = 0.8f;
	public int shotMilliseconds = 20;// ПЕРЕЗАРЯДКА своя
	protected float maxShotLifetime = 4.0F;
	protected float shotSize = 0.5f;

	private @Getter @Setter boolean active = true;
}
