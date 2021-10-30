package com.github.javlock.games.space.server.engine;

import static com.github.javlock.games.space.StaticData.asteroidMesh;
import static com.github.javlock.games.space.StaticData.broadphase;
import static com.github.javlock.games.space.StaticData.narrowphase;
import static com.github.javlock.games.space.StaticData.shipMesh;
import static com.github.javlock.games.space.StaticData.tmp;

import org.joml.GeometryUtils;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.GameEngine;
import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.event.space.EmitExplosionPacket;
import com.github.javlock.games.space.event.space.SpaceEntityDestroyEvent;
import com.github.javlock.games.space.event.space.SpaceEntitySpawnEvent;
import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;
import com.github.javlock.games.space.objects.space.entity.inspace.Asteroid;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.objects.space.entity.inspace.Shot;
import com.github.javlock.games.space.objects.space.entity.inspace.Turret;
import com.github.javlock.games.space.server.Server;
import com.github.javlock.games.space.server.config.engine.AsteroidConfig;
import com.github.javlock.games.space.server.config.engine.ShipConfig;
import com.github.javlock.games.space.server.header.ServerHeader;
import com.github.javlock.utils.NumberUtils;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import lombok.Getter;
import lombok.Setter;

public class ServerEngine extends GameEngine {

	private static float shipRadius = 4.0F;

	private static float maxShotLifetime = 4.0F;
	static float minAsteroidRadius = 1F;
	static float maxAsteroidRadius = 330F;

	Logger logger = LoggerFactory.getLogger("ServerEngine");

	private @Getter @Setter long lastTime = System.nanoTime();
	private @Getter Server server;
	private @Getter EventBus worldEventBus = new EventBus();

	// ########################################################
	// ########################################################ПОДГОТОВКА

	public ServerEngine(Server server) {
		this.server = server;
	}

	public void init() {
		logger.info("init-start");
		registerBus();

		AsteroidConfig asteroidConfig = ServerHeader.getServerConfig().getEngineConfig().getAsteroidConfig();

		for (int i = 0; i < asteroidConfig.getAsteroidCount(); i++) {
			logger.info("Ast:{}", i);
			try {
				Asteroid asteroid = new Asteroid();
				asteroid.setScale(Asteroid.generateSize(minAsteroidRadius, maxAsteroidRadius));

				double xmin = asteroidConfig.getRespawnRangeXmin();
				double xmax = asteroidConfig.getRespawnRangeXmax();
				asteroid.getPosition().x = NumberUtils.randomDoubleInRange(xmin, xmax);

				double ymin = asteroidConfig.getRespawnRangeYmin();
				double ymax = asteroidConfig.getRespawnRangeYmax();
				asteroid.getPosition().y = NumberUtils.randomDoubleInRange(ymin, ymax);

				double zmin = asteroidConfig.getRespawnRangeZmin();
				double zmax = asteroidConfig.getRespawnRangeZmax();
				asteroid.getPosition().z = NumberUtils.randomDoubleInRange(zmin, zmax);

				asteroid.setHealth(10);
				asteroid.setWorldEventBus(worldEventBus);
				localAsteroids.add(asteroid);

				StringBuilder msg = new StringBuilder();
				msg.append("size:").append(asteroid.getScale());
				msg.append(" X:").append(asteroid.getPosition().x);
				msg.append(" Y:").append(asteroid.getPosition().y);
				msg.append(" Z:").append(asteroid.getPosition().z);
				logger.info(msg.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new Thread((Runnable) () -> {
			logger.info("{} is started", Thread.currentThread().getName());
			while (isActive()) {
				try {
					ShipConfig shipConfig = ServerHeader.getServerConfig().getEngineConfig().getShipConfig();

					if (localShips.size() >= shipConfig.getShipCount()) {
						Thread.sleep(shipConfig.getShipRespawnDelay());
						continue;
					}
					Ship ship = new Ship();
					ship.setLastShotTime(0);

					double xmin = shipConfig.getRespawnRangeXmin();
					double xmax = shipConfig.getRespawnRangeXmax();
					ship.getPosition().x = NumberUtils.randomDoubleInRange(xmin, xmax);

					double ymin = shipConfig.getRespawnRangeYmin();
					double ymax = shipConfig.getRespawnRangeYmax();
					ship.getPosition().y = NumberUtils.randomDoubleInRange(ymin, ymax);

					double zmin = shipConfig.getRespawnRangeZmin();
					double zmax = shipConfig.getRespawnRangeZmax();
					ship.getPosition().z = NumberUtils.randomDoubleInRange(zmin, zmax);

					ship.setWorldEventBus(worldEventBus);

					server.getNetworkHandler().sendBroadcast(ship);
					localShips.add(ship);

					Turret turret = new Turret();
					turret.setWorldEventBus(worldEventBus);

					turret.getPosition().x = ship.getPosition().x;
					turret.getPosition().y = ship.getPosition().y;
					turret.getPosition().z = ship.getPosition().z;

					server.getNetworkHandler().sendBroadcast(turret);
					turrets.add(turret);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}, "localShips respawn").start();

		logger.info("init-end");
	}

	private void loop() {
		logger.info("loop-start");
		new Thread((Runnable) () -> {
			do {

				for (Asteroid asteroidBy : localAsteroids) {
					Vector3d projectilePosition = asteroidBy.getPosition();

					try {
						Vector3d newPosition = new Vector3d();

						for (Asteroid asteroid2 : localAsteroids) {
							if (asteroid2 == null) {
								localAsteroids.remove(asteroid2);
								continue;
							}
							if (asteroidBy == asteroid2) {
								continue;
							}

							Vector3f tmp2 = new Vector3f();
							// СТОЛКНОВЕНИЕ СНАРЯДА С АСТЕРОЙДОМ
							if (broadphase(asteroid2.getPosition().x, asteroid2.getPosition().y,
									asteroid2.getPosition().z, asteroidMesh.boundingSphereRadius, asteroid2.getScale(),
									projectilePosition, newPosition)
									&& narrowphase(asteroidMesh.positions, asteroid2.getPosition().x,
											asteroid2.getPosition().y, asteroid2.getPosition().z, asteroid2.getScale(),
											projectilePosition, newPosition, tmp,
											// FIXME StaticData.tmp2
											tmp2)) {

								if (asteroidBy.damage(1.0F)) {
									localAsteroids.remove(asteroidBy);
									SpaceEntityDestroyEvent spaceEntityDestroyEvent1 = new SpaceEntityDestroyEvent();
									spaceEntityDestroyEvent1.setTargetEntity(asteroidBy);

									server.getNetworkHandler().sendBroadcast(spaceEntityDestroyEvent1);
									System.err
											.println(asteroidBy.getId() + ":" + asteroidBy.getScale() + " столкнулся");
								}
								if (asteroid2.damage(1.0F)) {
									localAsteroids.remove(asteroid2);
									SpaceEntityDestroyEvent spaceEntityDestroyEvent2 = new SpaceEntityDestroyEvent();
									spaceEntityDestroyEvent2.setTargetEntity(asteroid2);

									server.getNetworkHandler().sendBroadcast(spaceEntityDestroyEvent2);
									System.err.println(asteroid2.getId() + ":" + asteroid2.getScale() + " столкнулся");
								}

							}
						}

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			} while (isActive());
		}, "astero").start();
		while (isActive()) {
			long thisTime = System.nanoTime();
			float deltaTime = (thisTime - lastTime) / 1E9f;
			lastTime = thisTime;

			update(deltaTime);
			for (Ship localShip : localShips) {
				shootFromShip(thisTime, localShip);
			}
		}
		logger.info("loop-end");
	}

	private void registerBus() {
		worldEventBus.register(this);
	}

	// ########################################################
	@Override
	public void run() {
		Thread.currentThread().setName("ServerEngine");
		logger.info("run-start");
		loop();
		logger.info("run-end");
	}

	// SHOOT
	private void shootFromShip(long thisTime, Ship shipWho) {
		try {
			if (shipWho == null) {
				return;
			}
			if (thisTime - shipWho.lastShotTime < 1E6 * GameEngine.shotOpponentMilliseconds) {
				return;
			}

			Vector3d shipPos = shipWho.getPosition();
			double shipPosX = shipPos.x;
			double shipPosY = shipPos.y;
			double shipPosZ = shipPos.z;

			// WTF
			Vector3f tmp3 = new Vector3f();
			Vector3f tmp4 = new Vector3f();

			shipWho.lastShotTime = thisTime;

			if (localShips.size() <= 1) {
				return;
			}

			// TARGET
			Ship targetShip = localShips.get(NumberUtils.randomIntInRange(0, localShips.size() - 1));

			Vector3d position = targetShip.getPosition();
			Vector3f linearVel = targetShip.getLinearVel();

			Vector3d shotPosVVector3d = tmp.set(shipPosX, shipPosY, shipPosZ).sub(position).negate().normalize()
					.mul(1.01f * shipRadius).add(shipPosX, shipPosY, shipPosZ);
			Vector3f icept = StaticData.intercept(shotPosVVector3d, GameEngine.shotVelocity, position, linearVel,
					StaticData.tmp2);

			if (icept == null) {
				return;
			} // jitter the direction a bit

			GeometryUtils.perpendicular(icept, tmp3, tmp4);
			icept.fma(((float) Math.random() * 2.0F - 1.0F) * 0.01f, tmp3);
			icept.fma(((float) Math.random() * 2.0F - 1.0F) * 0.01f, tmp4);
			icept.normalize();

			Shot newShot = new Shot();
			Vector3d projectilePosition = newShot.getPosition();
			Vector4f projectileVelocity = newShot.getProjectileVelocity();
			if (projectileVelocity.w <= 0.0F) {
				projectilePosition.set(shotPosVVector3d);
				projectileVelocity.x = StaticData.tmp2.x * GameEngine.shotVelocity;
				projectileVelocity.y = StaticData.tmp2.y * GameEngine.shotVelocity;
				projectileVelocity.z = StaticData.tmp2.z * GameEngine.shotVelocity;
				projectileVelocity.w = 0.01f;
			}
			directShots.add(newShot);
			server.getNetworkHandler().sendBroadcast(newShot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// SHOOT

	@Subscribe
	private void spawnEvent(SpaceEntitySpawnEvent spawnEvent) {
		SpaceEntity entity = spawnEvent.getTargetEntity();
		if (entity instanceof Asteroid) {
			Asteroid asteroid = (Asteroid) entity;
			localAsteroids.add(asteroid);
			logger.info("появился {} с scale {}", asteroid.getId(), asteroid.getScale());

			server.getNetworkHandler().sendBroadcast(asteroid);
		}
	}

	// UPDATE
	private void update(float deltaTime) {
		updateShots(deltaTime);
		updateRockets(deltaTime);
	}

	// updateRockets
	private void updateRockets(float deltaTime) {
	}

	private void updateShots(float deltaTime) {
		projectiles:

		for (Shot shot : directShots) {
			Vector4f projectileVelocity = shot.getProjectileVelocity();
			if (projectileVelocity.w <= 0.0F) {
				directShots.remove(shot);
				continue;
			}
			projectileVelocity.w += deltaTime;
			if (projectileVelocity.w > maxShotLifetime) {
				projectileVelocity.w = 0.0F;
				directShots.remove(shot);
				continue;
			}

			Vector3d projectilePosition = shot.getPosition();
			Vector3d newPosition = new Vector3d();

			newPosition.set(projectileVelocity.x, projectileVelocity.y, projectileVelocity.z).mul(deltaTime)
					.add(projectilePosition);

			/* Test against ships */

			for (Ship ship : localShips) {
				if (ship == null) {
					continue;
				}
				// СТОЛКНОВЕНИЕ СНАРЯДА С Кораблями
				if (broadphase(ship.getPosition().x, ship.getPosition().y, ship.getPosition().z,
						shipMesh.boundingSphereRadius, shipRadius, projectilePosition, newPosition)
						&& narrowphase(shipMesh.positions, ship.getPosition().x, ship.getPosition().y,
								ship.getPosition().z, shipRadius, projectilePosition, newPosition, tmp,
								StaticData.tmp2)) {

					if (ship.damage(1.0F)) {
						localShips.remove(ship);
						System.err.println(ship + " уничтожен");
						SpaceEntityDestroyEvent spaceEntityDestroyEvent = new SpaceEntityDestroyEvent();
						spaceEntityDestroyEvent.setTargetEntity(ship);

						server.getNetworkHandler().sendBroadcast(spaceEntityDestroyEvent);
					}

					EmitExplosionPacket emitExplosionPacket = new EmitExplosionPacket();
					emitExplosionPacket.setP(tmp);
					emitExplosionPacket.setNormal(null);
					server.getNetworkHandler().sendBroadcast(emitExplosionPacket);

					projectileVelocity.w = 0.0F;
					continue projectiles;
				} // СТОЛКНОВЕНИЕ СНАРЯДА С Кораблями
			}
			/* Test against asteroids */
			for (Asteroid asteroid : localAsteroids) {
				if (asteroid == null) {
					continue;
				}
				Vector3f tmp2 = new Vector3f();
				Vector3d asteroidPos = asteroid.getPosition();
				double asteroidPosX = asteroidPos.x;
				double asteroidPosY = asteroidPos.y;
				double asteroidPosZ = asteroidPos.z;

				// СТОЛКНОВЕНИЕ СНАРЯДА С АСТЕРОЙДОМ
				if (broadphase(asteroidPosX, asteroidPosY, asteroidPosZ, asteroidMesh.boundingSphereRadius,
						asteroid.getScale(), projectilePosition, newPosition)
						&& narrowphase(asteroidMesh.positions, asteroid.getPosition().x, asteroid.getPosition().y,
								asteroid.getPosition().z, asteroid.getScale(), projectilePosition, newPosition, tmp,
								// FIXME StaticData.tmp2
								tmp2)) {

					if (asteroid.damage(1.0F)) {
						localAsteroids.remove(asteroid);
						System.err.println(asteroid + " уничтожен");
						SpaceEntityDestroyEvent spaceEntityDestroyEvent = new SpaceEntityDestroyEvent();
						spaceEntityDestroyEvent.setTargetEntity(asteroid);

						server.getNetworkHandler().sendBroadcast(spaceEntityDestroyEvent);
					}
					EmitExplosionPacket emitExplosionPacket = new EmitExplosionPacket();
					emitExplosionPacket.setP(tmp);
					emitExplosionPacket.setNormal(
							// FIXME StaticData.tmp2
							tmp2);// FIXME
					server.getNetworkHandler().sendBroadcast(emitExplosionPacket);

					projectileVelocity.w = 0.0F;
					continue projectiles;
				} // СТОЛКНОВЕНИЕ СНАРЯДА С АСТЕРОЙДОМ

			}
			projectilePosition.set(newPosition);
		}
	}
	// updateRockets

	// UPDATE
}
