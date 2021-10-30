package com.github.javlock.games.space.objects.space.entity.basic;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.github.javlock.games.space.event.space.SpaceEntityDestroyEvent;
import com.github.javlock.games.space.event.space.SpaceEntitySpawnEvent;
import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.utils.NumberUtils;
import com.google.common.eventbus.EventBus;

import lombok.Getter;
import lombok.Setter;

public class SpaceEntity implements Serializable, SpaceEntityInterface {
	private static final long serialVersionUID = 3343374412307380905L;

	private @Getter @Setter int id;

	private @Getter @Setter float scale = 1F;

	private @Getter @Setter float health = 10.0F;

	private @Getter Vector3d position = new Vector3d(0, 0, 10);
	private @Getter @Setter Vector3f linearVel = new Vector3f();
	private @Getter Vector4f projectileVelocity = new Vector4f(0, 0, 0, 0);

	private transient @Getter @Setter EventBus worldEventBus;
	private transient @Getter @Setter EventBus commandEventBus;

	public SpaceEntity() {
		setId(NumberUtils.randomIntInRange(1, Integer.MAX_VALUE));
	}

	@Override
	public boolean damage(float dmg) {
		health = health - dmg;
		if (health <= 0.0F) {
			destroy();
			return true;
		}
		return false;
	}

	@Override
	public void destroy() {
		if (health != 0F) {
			health = 0F;
		}

		SpaceEntityDestroyEvent destroyEvent = new SpaceEntityDestroyEvent();
		destroyEvent.setTargetEntity(this);
		worldEventBus.post(destroyEvent);
		spawnResource();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpaceEntity other = (SpaceEntity) obj;
		return id == other.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public boolean removeFromList(CopyOnWriteArrayList<Ship> localShips) {
		for (Ship ship : localShips) {
			if (id == ship.getId()) {
				localShips.remove(ship);
				return true;
			}
		}
		return false;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	@Override
	public void spawn() {
		SpaceEntitySpawnEvent spawnEvent = new SpaceEntitySpawnEvent();
		spawnEvent.setTargetEntity(this);
		worldEventBus.post(spawnEvent);
	}

	@Override
	public void spawnResource() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpaceEntity [id=");
		builder.append(id);
		builder.append(", health=");
		builder.append(health);
		builder.append(", position=");
		builder.append(position);
		builder.append(", linearVel=");
		builder.append(linearVel);
		builder.append(", projectileVelocity=");
		builder.append(projectileVelocity);
		builder.append("]");
		return builder.toString();
	}

	//
	@Override
	public void updatePosition(double x, double y, double z) {
		position.x = position.x + x;
		position.y = position.y + y;
		position.z = position.z + z;
	}

}
