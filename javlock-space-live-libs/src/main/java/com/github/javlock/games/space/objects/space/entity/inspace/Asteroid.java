package com.github.javlock.games.space.objects.space.entity.inspace;

import java.util.Objects;

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
		if (getWorldEventBus() != null) {

			// объем текущего
			double currentV = getV();
			// размер текущего
			float currentScale = getScale();
			// позиция текущего
			double currentX = getPosition().x;
			double currentY = getPosition().y;
			double currentZ = getPosition().z;

			// радиус текущего
			double currentR = Math.cbrt(currentV);
			// разброс
			int xrd = (int) (currentR * currentR * 3.05F);

			while (currentV >= 4) {

				// новый объем (от 1/50 до 1/5 от старого) (потом вычитаем новый из currentV)
				double newV = NumberUtils.randomDoubleInRange(currentScale / 50, currentV / 5);
				if (newV > currentV) {
					continue;
				}
				currentV = currentV - newV;// вычитаем

				// новый scale (получаем из "новый объем")
				float newScale = getScaleByV(newV);

				// новая позиция (должен находиться в объеме старого, возможен разброс )
				// позиция координат исходя из объема (пары)
				double newRXmin = currentX - currentR - xrd;
				double newRXmax = currentX + currentR + xrd;

				double newRYmin = currentY - currentR - xrd;
				double newRYmax = currentY + currentR + xrd;

				double newRZmin = currentZ - currentR - xrd;
				double newRZmax = currentZ + currentR + xrd;

				// [-x-:]

				// новая позиция
				double newX = NumberUtils.randomDoubleInRange(newRXmin, newRXmax);
				double newY = NumberUtils.randomDoubleInRange(newRYmin, newRYmax);
				double newZ = NumberUtils.randomDoubleInRange(newRZmin, newRZmax);

				//

				Asteroid newAsteroid = new Asteroid();
				newAsteroid.setWorldEventBus(getWorldEventBus());
				newAsteroid.setHealth(newScale * 5);
				newAsteroid.setScale(newScale);

				// позиция нового

				newAsteroid.getPosition().x = newX;
				newAsteroid.getPosition().y = newY;
				newAsteroid.getPosition().z = newZ;

				newAsteroid.spawn();
			}
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