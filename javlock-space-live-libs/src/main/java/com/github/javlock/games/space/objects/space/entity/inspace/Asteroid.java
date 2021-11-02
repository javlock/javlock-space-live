package com.github.javlock.games.space.objects.space.entity.inspace;

import java.util.Objects;
import java.util.Random;

import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;
import com.github.javlock.utils.NumberUtils;

public class Asteroid extends SpaceEntity {

	private static final long serialVersionUID = -3209789085544875041L;

	public static transient int asteroidPositionVbo;
	public static transient int asteroidNormalVbo;

	public static float generateCoord(float size, float a, float b) {
		return NumberUtils.randomFloatInRange(0, a / b * size);
	}

	public static float generateSize(float min, float max) {
		return NumberUtils.randomFloatInRange(min, max);
	}

	public Asteroid() {
		super();
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
		return getId() == other.getId();
	}

	private float getScaleByV(double randomValue) {
		double st1 = 3000 * randomValue;
		double st2 = 4 * Math.PI;
		double st3 = st1 / st2;
		return (float) Math.cbrt(st3);
	}

	private double getV() {
		return 4D / 3D * Math.PI * Math.pow(getScale(), 3) / 1000;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public void spawnResource() {
		double vOrig = getV();
		LoggerFactory.getLogger("TEST").info(":{}", vOrig);
		double currentV = vOrig;
		Random r = new Random();
		int rangeMin = 40;
		int rangeMax = -60;
		while (currentV >= 30) {

			double randomValue = NumberUtils.randomDoubleInRange(rangeMin, currentV);
			currentV = currentV - randomValue;

			Asteroid newAsteroid = new Asteroid();
			newAsteroid.setWorldEventBus(getWorldEventBus());
			newAsteroid.setHealth(10);

			float newScale = getScaleByV(randomValue);
			newAsteroid.setScale(newScale);

			int base = 600;
			base = (int) (base + randomValue) + 13;

			newAsteroid.getPosition().x = getPosition().x + NumberUtils.randomDoubleInRange(-base, base);
			newAsteroid.getPosition().y = getPosition().y + NumberUtils.randomDoubleInRange(-base, base);
			newAsteroid.getPosition().z = getPosition().z + NumberUtils.randomDoubleInRange(-base, base);

			newAsteroid.spawn();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Asteroid [scale=");
		builder.append(getScale());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getPosition()=");
		builder.append(getPosition());
		builder.append(", getHealth()=");
		builder.append(getHealth());
		builder.append(", getLinearVel()=");
		builder.append(getLinearVel());
		builder.append(", getProjectileVelocity()=");
		builder.append(getProjectileVelocity());
		builder.append("]");
		return builder.toString();
	}
}