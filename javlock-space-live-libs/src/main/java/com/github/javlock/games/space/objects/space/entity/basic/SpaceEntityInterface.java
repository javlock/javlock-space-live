package com.github.javlock.games.space.objects.space.entity.basic;

public interface SpaceEntityInterface {

	public boolean damage(float dmg);

	public void destroy();

	public void setPosition(double x, double y, double z);

	public void spawn();

	public void spawnResource();

	public void updatePosition(double x, double y, double z);

}
