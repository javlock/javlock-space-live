package com.github.javlock.games.space.server.header;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.javlock.games.space.objects.space.entity.inspace.Ship;
import com.github.javlock.games.space.server.config.ServerConfig;

import lombok.Getter;
import lombok.Setter;

public final class ServerHeader {
	@Getter
	private static File rootDir = new File("javlock_games_space_server");

	@Getter
	private static File configFile = new File(getRootDir(), "ServerConfig.yml");
	@Getter
	@Setter
	private static ServerConfig serverConfig = new ServerConfig();

	@Getter
	private static CopyOnWriteArrayList<Ship> ships = new CopyOnWriteArrayList<>();

	private ServerHeader() {
	}
}
