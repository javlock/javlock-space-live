package com.github.javlock.games.space.objects.space.entity.basic;

import org.joml.Vector4d;

import lombok.Getter;
import lombok.Setter;

public class Particle extends SpaceEntity {

	private static final long serialVersionUID = -284148279504352864L;
	public static int particle_projUniform;
	public static int particleProgram;

	// private @Getter @Setter Vector3d particlePosition = new Vector3d(0, 0, 0);
	private @Getter @Setter Vector4d particleVelocity = new Vector4d(0, 0, 0, 0);

}
