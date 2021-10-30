package com.github.javlock.games.space.network.handler;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class JSONRecHandler extends ChannelDuplexHandler {
	private static final String PART0 = "class:[";
	private static final String PART1 = "]:class#_#data:[";
	private static final String PART2 = "]:data";

	ObjectMapper mapper = new ObjectMapper(new JsonFactory());

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		Object obj = jsonToObj(msg);
		super.channelRead(ctx, obj);
	}

	// PART0 +className+PART1+data+PART2
	// class:[ +className+ ]:class#_#data:[ +data+ ]:data
	public Object jsonToObj(Object msg) throws ClassNotFoundException, JsonProcessingException {
		String objectAsString = (String) msg;
		String classNameFromString = StringUtils.substringBetween(objectAsString, PART0, PART1);
		String data = StringUtils.substringBetween(objectAsString, PART1, PART2);
		Class<?> clasS = Class.forName(classNameFromString);
		Object newObj = mapper.readValue(data, clasS);
		return clasS.cast(newObj);
	}

	private String objToJson(Object msg) throws JsonProcessingException {
		String className = msg.getClass().getName();
		String dataSring = mapper.writer().writeValueAsString(msg);
		return PART0 + className + PART1 + dataSring + PART2 + "\n";
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		String json = objToJson(msg);
		super.write(ctx, json, promise);
	}
}