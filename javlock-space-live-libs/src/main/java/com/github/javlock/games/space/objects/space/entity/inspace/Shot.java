package com.github.javlock.games.space.objects.space.entity.inspace;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;

public class Shot extends SpaceEntity {

	private static final long serialVersionUID = 7672392392762991678L;

	public static int shot_projUniform;

	public static int shotProgram;

	public static final float SHOTVELOCITY = 240.0F;
	public static final transient int SHOTMILLISECONDS = 20;// ПЕРЕЗАРЯДКА
	public static final transient float MAXSHOTLIFETIME = 4.0F;

	int damage = 1;

	public Shot() throws NoSuchAlgorithmException {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Shot other = (Shot) obj;
		return getId() == other.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(getId());
		return result;
	}
}
