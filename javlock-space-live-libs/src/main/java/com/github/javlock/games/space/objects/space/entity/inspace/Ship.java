package com.github.javlock.games.space.objects.space.entity.inspace;

import java.security.NoSuchAlgorithmException;

import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class Ship extends SpaceEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = 8802711377991560305L;

	@Getter
	@Setter
	public long lastShotTime;

	public Ship() throws NoSuchAlgorithmException {
		super();
	}

	@Override
	public float getScale() {
		return 4F;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Ship [getId()=");
		builder.append(getId());
		builder.append(", getHealth()=");
		builder.append(getHealth());
		builder.append(", getPosition()=");
		builder.append(getPosition());
		builder.append(", getLinearVel()=");
		builder.append(getLinearVel());
		builder.append(", getProjectileVelocity()=");
		builder.append(getProjectileVelocity());
		builder.append("]");
		return builder.toString();
	}

}