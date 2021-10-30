package com.github.javlock.games.space.server;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.ServiceConfigurationError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.javlock.games.space.StaticData;
import com.github.javlock.games.space.network.packets.Packet;
import com.github.javlock.games.space.server.config.ServerConfig;
import com.github.javlock.games.space.server.engine.ServerEngine;
import com.github.javlock.games.space.server.header.ServerHeader;
import com.github.javlock.games.space.server.network.NetworkHandler;
import com.github.javlock.games.space.server.network.handler.ObjectHandlerServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.Getter;

public class Server {
	static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private @Getter ServerEngine serverEngine = new ServerEngine(this);

	private static @Getter ServerBootstrap serverBootstrap = new ServerBootstrap();
	private static @Getter EventLoopGroup serverWorkgroup = new NioEventLoopGroup();

	private int port;
	Server server = this;

	// ########################################################
	// network
	private @Getter NetworkHandler networkHandler = new NetworkHandler(this);

	private ChannelFuture bindChannelFuture;

	public void initConfig() throws IOException {
		logger.info("initConfig-start");
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		// CHECK and create
		if (!ServerHeader.getConfigFile().exists()) {
			if (!ServerHeader.getConfigFile().getParentFile().exists()) {
				ServerHeader.getConfigFile().getParentFile().mkdirs();
			}
			if (ServerHeader.getConfigFile().createNewFile()) {

			}
			mapper.writeValue(ServerHeader.getConfigFile(), ServerHeader.getServerConfig());
			throw new ServiceConfigurationError("please edit " + ServerHeader.getConfigFile().getAbsolutePath());
		}
		// READ
		ServerHeader.setServerConfig(mapper.readValue(ServerHeader.getConfigFile(), ServerConfig.class));
		if (!ServerHeader.getServerConfig().isPrepare()) {
			throw new ServiceConfigurationError("please edit " + ServerHeader.getConfigFile().getAbsolutePath());
		}
		logger.info("initConfig-end");
	}

	public void initData() throws IOException {
		logger.info("initData-start");
		StaticData.init();
		logger.info("initData-end");
	}

	public void initGame() {
		logger.info("initGame-start");
		serverEngine.init();
		logger.info("initGame-end");
	}

	public void initNetwork() {

		port = ServerHeader.getServerConfig().getNetworkConfig().getServerPort();

		logger.info("initNetwork-new-start");
		serverBootstrap.group(serverWorkgroup).channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port));
		serverBootstrap.option(ChannelOption.SO_REUSEADDR, true);

		serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();

				p.addLast(new ObjectDecoder(Integer.MAX_VALUE,
						ClassResolvers.softCachingConcurrentResolver(Packet.class.getClassLoader())));
				p.addLast(new ObjectEncoder());

				// p.addLast(new JSONRecHandler());
				p.addLast(new ObjectHandlerServer(server));
			}
		});
		logger.info("initNetwork-new-end");
	}

	public void startGame() {
		logger.info("startGame-start");
		serverEngine.start();
		logger.info("startGame-end");
	}

	public void startNetwork() throws IOException {
		logger.info("startNetwork-start");
		/*
		 * boolean error = false; do { try {
		 * NetWorkHeader.getTcpHandler().getAcceptor().bind(new
		 * InetSocketAddress(port)); logger.info("Server:startNetwork:TCP:STARTED");
		 * error = false; } catch (Exception e) {
		 * logger.info("startNetwork-error: [{}] by [{}]", e.getMessage(),
		 * e.getCause().getMessage()); error = true; try { Thread.sleep(5000); } catch
		 * (Exception e2) { // IGNORE } } } while (error);
		 */

		bindChannelFuture = serverBootstrap.bind();
		logger.info("startNetwork-end");

	}

}
