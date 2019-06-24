package com.officeten.omc.netty;

import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Jin
 * @date 2019/6/24
 */
@Component
@Order(1)
public class ServerCommandLineRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(ServerCommandLineRunner.class);

	@Autowired
	private SocketIOServer server;

	@Override
	public void run(String... args) throws Exception {
		server.start();
		logger.info("socket.io start!");
	}
}
