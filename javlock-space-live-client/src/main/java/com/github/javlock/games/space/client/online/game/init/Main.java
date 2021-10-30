package com.github.javlock.games.space.client.online.game.init;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.client.online.game.header.GameHeader;

public class Main {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {

		logger.info("main-start");
		StaticData.initSecurity();
		GameHeader.onlinegame.initConfig();
		GameHeader.onlinegame.initData();

		GameHeader.onlinegame.startNetwork();
		GameHeader.onlinegame.startGame();

		GameHeader.onlinegame.exit();
		GameHeader.onlinegame.setActive(false);
		logger.info("main-end");
	}

}
