package com.github.javlock.games.space.objects.space.entity.basic;

import org.joml.Vector3d;
import org.joml.Vector4d;

import lombok.Getter;
import lombok.Setter;

public class VectorsDataObjectPairD {
	@Getter
	@Setter
	Vector3d particlePosition = new Vector3d(0, 0, 0);
	@Getter
	@Setter
	Vector4d particleVelocity = new Vector4d(0, 0, 0, 0);
}
