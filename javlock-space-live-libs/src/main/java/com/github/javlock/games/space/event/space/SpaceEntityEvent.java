package com.github.javlock.games.space.event.space;

import com.github.javlock.games.space.objects.space.entity.basic.SpaceEntity;

import lombok.Getter;
import lombok.Setter;

public class SpaceEntityEvent extends SpaceEvent {
	private static final long serialVersionUID = 4184058426407735L;

	private @Getter @Setter SpaceEntity targetEntity;
}
