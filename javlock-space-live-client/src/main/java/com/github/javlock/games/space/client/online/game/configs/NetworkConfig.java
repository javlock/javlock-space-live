package com.github.javlock.games.space.client.online.game.configs;

import com.github.javlock.games.space.network.ProtoType;

import lombok.Getter;
import lombok.Setter;

public class NetworkConfig {

	private @Getter @Setter String serverHost = "127.0.0.1";

	private @Getter @Setter int serverPort = 23000;
	private @Getter @Setter ProtoType protoType = ProtoType.TCP;
}
