package com.github.javlock.games.space.objects.inship;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.naming.LimitExceededException;

import lombok.Getter;
import lombok.Setter;

public class Inventory implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 9004690403290666062L;
	@Getter
	@Setter
	private int maxSize;
	private CopyOnWriteArrayList<Item> items = new CopyOnWriteArrayList<>();

	public void addItem(Item item) throws LimitExceededException {
		if (items.size() >= maxSize) {
			throw new LimitExceededException();
		}
		items.add(item);
	}

	public CopyOnWriteArrayList<Item> returnAllItems() {
		return new CopyOnWriteArrayList<>(items);
	}
}
