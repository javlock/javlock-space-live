package com.github.javlock.games.space.server.config.network;

import com.github.javlock.games.space.network.ProtoType;

import lombok.Getter;
import lombok.Setter;

public class NetworkConfig {

	@Getter
	@Setter
	private ProtoType proto = ProtoType.TCP;

	@Getter
	@Setter
	private int serverPort = 23000;
	@Getter
	@Setter
	private int timeOut = 10000;
}
