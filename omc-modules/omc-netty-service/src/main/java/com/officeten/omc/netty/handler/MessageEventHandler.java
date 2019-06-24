package com.officeten.omc.netty.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.officeten.omc.model.socket.entity.ChatMessage;
import com.officeten.omc.model.socket.entity.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 消息事件处理器
 * @author Jin
 * @date 2019/6/24
 */
@Component
public class MessageEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);

	private final SocketIOServer server;

	private HashMap<String, SocketIOClient> sessionMap = new HashMap<>();

	@Autowired
	public MessageEventHandler(SocketIOServer server) {
		this.server = server;
	}

	/**
	 * 添加connect事件，当客户端发起连接时调用
	 * @param client
	 */
	@OnConnect
	public void onConnect(SocketIOClient client) {

		if (client != null) {
			String username = client.getHandshakeData().getSingleUrlParam("username");
			String password = client.getHandshakeData().getSingleUrlParam("password");
			String sessionId = client.getSessionId().toString();
			sessionMap.put(username, client);
			logger.info("连接成功, username=" + username + ", password=" + password + ", sessionId=" + sessionId);
		} else {
			logger.error("客户端为空");
		}
	}

	/**
	 * 添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
	 * @param client
	 */
	@OnDisconnect
	public void onDisconnect(SocketIOClient client) {
		logger.info("客户端断开连接, sessionId=" + client.getSessionId().toString());
		client.disconnect();
		sessionMap.remove(client.getHandshakeData().getSingleUrlParam("username"));
	}

	/**
	 * 消息接收入口
	 * @param client
	 * @param ackRequest
	 * @param chat
	 */
	@OnEvent(value = "chatevent")
	public void onEvent(SocketIOClient client, AckRequest ackRequest, ChatMessage chat) {
		logger.info("接收到客户端消息");

		if (ackRequest.isAckRequested()) {
			// send ack response with data to client
			ackRequest.sendAckData("服务器回答chatevent, userName=" + chat.getUserName() + ",message=" + chat.getMessage());

			for (SocketIOClient socketIOClient : sessionMap.values()) {
				socketIOClient.sendEvent("chatevent", "userName=" + chat.getUserName() + ",message=" + chat.getMessage());
			}
		}
	}

	/**
	 * 登录接口
	 * @param client
	 * @param ackRequest
	 * @param message
	 */
	@OnEvent(value = "login")
	public void onLogin(SocketIOClient client, AckRequest ackRequest, LoginRequest message) {
		logger.info("接收到客户端登录消息");

		if (ackRequest.isAckRequested()) {
			// send ack response with data to client
			ackRequest.sendAckData("服务器回答login", message.getCode(), message.getBody());
		}
	}
}
