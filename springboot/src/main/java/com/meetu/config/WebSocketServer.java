package com.meetu.config;

import java.io.Console;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.meetu.chatroom.MessageInfo;
import com.meetu.model.chatroom.ChatroomMessage;
import com.meetu.service.ChatroomService;

import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ServerEndpoint("/notice/{userId}")
public class WebSocketServer {

	/**
	 * 解决无法注入bean：定义静态service对象，通过@Autowired在系统启动时为静态变量赋值
	 * 
	 * @Autowired 注解作用在方法上，如果方法没有参数，spring容器会在类加载完后执行一次这个方法，
	 *            如果方法中有参数的话，还会从容器中自动注入这个方法的参数，然后执行一次这个方法。
	 */
	private static ChatroomService cService;

	@Autowired
	public void setChatroomService(ChatroomService chatroomService) {
		WebSocketServer.cService = chatroomService;
	}

	// 存储所有的会话
	private static Map<String, Session> clients = new ConcurrentHashMap<>();
	// 存储用户ID到多个会话ID的映射
	private static Map<String, Set<String>> connection = new ConcurrentHashMap<>();

	private String sid = null;
	private String userId;

	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId, EndpointConfig config) {
		this.sid = java.util.UUID.randomUUID().toString();
		this.userId = userId;
		clients.put(this.sid, session);
		Set<String> clientSet = connection.computeIfAbsent(this.userId, k -> ConcurrentHashMap.newKeySet());
		clientSet.add(this.sid);
		log.info("User " + this.userId + " established connection, session " + this.sid + " opened!");
	}

	@OnClose
	public void onClose() {
		clients.remove(this.sid);
		Set<String> clientSet = connection.get(this.userId);
		if (clientSet != null) {
			clientSet.remove(this.sid);
			if (clientSet.isEmpty()) {
				connection.remove(this.userId);
			}
		}
		log.info("Session " + this.sid + " closed");
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("Received message from user " + this.userId + ": " + message);
		try {
			JSONObject jsonObject = new JSONObject(message);
			String type = jsonObject.getString("type");

			if ("heartbeat".equals(type)) {
				sendMessageToUser(this.userId,
						new MessageInfo(null, Integer.parseInt(this.userId), "heartbeat", "ok", null));
			} else if ("message".equals(type)) {
				String chatroomType = jsonObject.getString("chatroomType");
				Integer chatroomId = jsonObject.getInt("chatroomId");
				String content = jsonObject.optString("content", "");
				Instant timestamp = Instant.parse(jsonObject.optString("timestamp", Instant.now().toString()));
				ChatroomMessage userMessage = new ChatroomMessage(chatroomId, Integer.parseInt(this.userId), content,
						timestamp);
				MessageInfo messageInfo = new MessageInfo(chatroomId, Integer.parseInt(this.userId), "message", content,
						timestamp);
				if (cService != null) {
					if (chatroomType.equals("P")) {
						String receiverId = String.valueOf(jsonObject.getInt("receiverId"));
						sendMessageToUser(receiverId, messageInfo);
						sendMessageToUser(this.userId, messageInfo);
					} else if (chatroomType.equals("G")) {
						List<Integer> usersInChatroom = cService.getUsersInChatroom(chatroomId);
						for (Integer userId : usersInChatroom) {
							sendMessageToUser(String.valueOf(userId), messageInfo);
						}
					}
					cService.saveMessages(userMessage);
					cService.readAllMessage(chatroomId, Integer.parseInt(this.userId));

				} else {
					log.error("ChatroomService is not initialized");
				}
			}
		} catch (Exception e) {
			log.error("Error processing message", e);

		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.error("WebSocket error for session " + this.sid, error);
	}

	public static void sendMessageToUser(String userId, MessageInfo message) {
		Set<String> clientSet = connection.get(userId);
		if (clientSet != null) {
			for (String sid : clientSet) {
				Session session = clients.get(sid);
				if (session != null && session.isOpen()) {
					try {
						String jsonString = new JSONObject(message).toString();
						session.getBasicRemote().sendText(jsonString);
						System.out.println("成功傳送給:" + userId);
					} catch (IOException e) {
						log.error("Error sending message to session " + sid, e);
					}
				}
			}
		}
	}
}