package com.github.javlock.games.space.server.init;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.server.Server;

public class Main {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		Server server = new Server();
		StaticData.initSecurity();
		server.initConfig();
		server.initData();
		server.initNetwork();
		// START GAME
		server.initGame();
		server.startGame();

		// WAIT FOR CONNECTIONS
		server.startNetwork();
	}
}
