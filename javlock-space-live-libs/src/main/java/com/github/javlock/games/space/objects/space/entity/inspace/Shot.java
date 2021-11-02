package com.github.javlock.games.space.objects.space.entity.inspace;

import java.security.NoSuchAlgorithmException;

import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;

public class Shot extends SpaceEntity {
	/**
	 *
	 */
	private static final long serialVersionUID = 7672392392762991678L;

	public static int shot_projUniform;

	public static int shotProgram;

	int damage = 1;

	public Shot() throws NoSuchAlgorithmException {
		super();
	}
}
