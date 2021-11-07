package webSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;

@ServerEndpoint("/chatroomServerEndpoint")
public class ChatroomServerEndpoint {
	static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>());

	/**
	 * users will be added into Session <br>
	 * If users are in Session, They can see messages of all users
	 * 
	 * @throws IOException
	 */
	@OnOpen
	public void handleOpen(Session userSession) throws IOException {
		chatroomUsers.add(userSession);
		userSession.getBasicRemote().sendText(buildJsonData("System", "Enter your name first"));
	}

	/**
	 * The first input of user will be user's name in the chat box <br>
	 * Then next input is for chating <br>
	 */
	@OnMessage
	public void handleMessage(String message, Session userSession) throws IOException {
		String username = (String) userSession.getUserProperties().get("username");
		if (username == null) {
			userSession.getUserProperties().put("username", message);
			userSession.getBasicRemote().sendText(buildJsonData("System", "You are now connected as " + message));
		} else {
			Iterator<Session> iterator = chatroomUsers.iterator();
			while (iterator.hasNext())
				iterator.next().getBasicRemote().sendText(buildJsonData(username, message));
		}
	}

	/**
	 * If users close the website <br>
	 * The website will remove users from Session
	 */
	@OnClose
	public void handleClose(Session userSession) {
		chatroomUsers.remove(userSession);
	}

	/**
	 * Create a json text with username and message
	 * 
	 * @return json with username and message
	 */
	private static String buildJsonData(String username, String message) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("message", username + ": " + message);
		return jsonObject.toString();
	}

}
