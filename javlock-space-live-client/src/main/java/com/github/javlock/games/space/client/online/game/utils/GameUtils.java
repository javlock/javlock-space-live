package com.github.javlock.games.space.client.online.game.utils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.util.ServiceConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javlock.games.space.client.online.game.configs.ClientConfig;
import com.github.javlock.games.space.client.online.game.header.GameHeader;

public class GameUtils {
	private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void initConfig() throws IOException, NoSuchAlgorithmException {
		logger.info("initConfig-start");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		if (!GameHeader.getConfigFile().exists()) {
			if (!GameHeader.getConfigFile().getParentFile().exists()) {
				GameHeader.getConfigFile().getParentFile().mkdirs();
			}
			if (GameHeader.getConfigFile().createNewFile()) {// IGNORE
			}
			GameHeader.clientConfig.getPlayer().genNew();
			mapper.writeValue(GameHeader.getConfigFile(), GameHeader.clientConfig);
			throw new ServiceConfigurationError("please edit " + GameHeader.getConfigFile().getAbsolutePath());
		}
		GameHeader.setClientConfig(mapper.readValue(GameHeader.getConfigFile(), ClientConfig.class));
		if (!GameHeader.getClientConfig().isPrepare()) {
			throw new ServiceConfigurationError("please edit " + GameHeader.getConfigFile().getAbsolutePath());
		}
		logger.info("initConfig-end");

	}

}
