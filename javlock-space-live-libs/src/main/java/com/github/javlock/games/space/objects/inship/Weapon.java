package com.github.javlock.games.space.objects.inship;

import lombok.Getter;
import lombok.Setter;

public class Weapon {
	@Getter
	@Setter
	private int weaponPossition;
	@Getter
	@Setter
	private float shotVelocity = 450.0F;// скорость
	@Getter
	@Setter
	private float shotSeparation = 0.8f;
	@Getter
	@Setter
	private int shotMilliseconds = 20;// ПЕРЕЗАРЯДКА
}
