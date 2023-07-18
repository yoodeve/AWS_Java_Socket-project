package socket_project_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import socket_project_server.DTO.RequestBodyDTO;
import socket_project_server.DTO.SendMessage;
import socket_project_server.entity.Room;

@RequiredArgsConstructor
public class ConnectedSocket extends Thread {

	private final Socket socket;
	private Gson gson;

	private String username;

	@Override
	public void run() {
		gson = new Gson();

		while (true) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String requestBody = bufferedReader.readLine();

				requestController(requestBody);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void requestController(String requestBody) {
		String resource = gson.fromJson(requestBody, RequestBodyDTO.class).getResource();
		switch (resource) {
		case "connection":
			connection(requestBody);

			break;

		case "createRoom":
			createRoom(requestBody);

			break;

		case "enter":
			enter(requestBody);

			break;

		case "sendMessage":
			sendMessage(requestBody);

			break;

		case "exitRoom":
			exitRoom(requestBody);

			break;

		case "sendPrivateMessage":
			sendPrivateMessage(requestBody);

			break;
		}
	}

	private void connection(String requestBody) {
		username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		List<String> roomNameList = new ArrayList<>();
		ServerApp.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		RequestBodyDTO<List<String>> updateRoomRequestBodyDTO = new RequestBodyDTO<List<String>>("updateRoomList",
				roomNameList);
	}

	private void createRoom(String requestBody) {
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		Room newRoom = Room.builder().roomName(roomName).owner(username).userList(new ArrayList<ConnectedSocket>())
				.build();
		ServerApp.roomList.add(newRoom);

		List<String> roomNameList = new ArrayList<>();

		ServerApp.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});

		RequestBodyDTO<List<String>> updateRoomRequestBodyDto = new RequestBodyDTO<List<String>>("updateRoomList",
				roomNameList);
		ServerApp.connectedSocketList.forEach(con -> {
			ServerSender.getInstance().send(con.socket, updateRoomRequestBodyDto);
		});

	}

	private void enter(String requestBody) {
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		ServerApp.roomList.forEach(room -> {
			if (room.getRoomName().equals(roomName)) {
				room.getUserList().add(this);

				List<String> usernameList = new ArrayList<>();
				room.getUserList().forEach(con -> {
					usernameList.add(con.username);
				});
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDTO<List<String>> updateUserListDto = new RequestBodyDTO<List<String>>("updateUserList",
							usernameList);
					ServerSender.getInstance().send(connectedSocket.socket, updateUserListDto);
					RequestBodyDTO<String> joinMessageDto = new RequestBodyDTO<String>("sendMessage",
							"경쾌하게 " + username + "님 입장~!");
					ServerSender.getInstance().send(connectedSocket.socket, joinMessageDto);
				});

			}
		});
	}

	private void sendMessage(String requestBody) {
		TypeToken<RequestBodyDTO<SendMessage>> typeToken = new TypeToken<RequestBodyDTO<SendMessage>>() {
		};
		RequestBodyDTO<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
		SendMessage sendMessage = requestBodyDto.getBody();

		ServerApp.roomList.forEach(connectedSocket -> {
			if (connectedSocket.getUserList().contains(this)) {
				connectedSocket.getUserList().forEach(con -> {
					RequestBodyDTO<String> dto = new RequestBodyDTO<>("sendMessage",
							username + " : " + sendMessage.getMessageBody());
					ServerSender.getInstance().send(con.socket, dto);
				});

			}
		});

	}

	private void exitRoom(String requestBody) {
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		ServerApp.roomList.forEach(room -> {
			if (room.getRoomName().equals(roomName)) {
				room.getUserList().removeIf(connectedSocket -> connectedSocket == this);

				List<String> usernameList = new ArrayList<>();
				room.getUserList().forEach(con -> {
					usernameList.add(con.username);
				});
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDTO<List<String>> updateUserListDto = new RequestBodyDTO<List<String>>("updateUserList",
							usernameList);
					ServerSender.getInstance().send(connectedSocket.socket, updateUserListDto);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					RequestBodyDTO<String> exitMessageDto = new RequestBodyDTO<String>("sendMessage",
							"경쾌하게 " + username + "님 퇴장~!");
					ServerSender.getInstance().send(connectedSocket.socket, exitMessageDto);
				});
			}

		});

	}

	private void sendPrivateMessage(String requestBody) {
		TypeToken<RequestBodyDTO<SendMessage>> typeToken = new TypeToken<RequestBodyDTO<SendMessage>>() {
		};
		RequestBodyDTO<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
		SendMessage privateMessage = requestBodyDto.getBody();
		String receiverUsername = privateMessage.getToUsername();
		String privateMessageContent = privateMessage.getMessageBody();

		ServerApp.connectedSocketList.forEach(con -> {
			if (con.username.equals(receiverUsername)) {
				String senderUsername = this.username;
				String fullMessageContent = "[" + senderUsername + "]" + "님의 귓속말: " + privateMessageContent;
				RequestBodyDTO<String> privateMessageDto = new RequestBodyDTO<>("receivePrivateMessage",
						fullMessageContent);
				ServerSender.getInstance().send(con.socket, privateMessageDto);

				String senderFullMessageContent = "[나 -> " + receiverUsername + "]" + "님에게 귓속말: "
						+ privateMessageContent;
				RequestBodyDTO<String> senderPrivateMessageDto = new RequestBodyDTO<>("receivePrivateMessage",
						senderFullMessageContent);
				ServerSender.getInstance().send(socket, senderPrivateMessageDto);

			}
		});

	}
}
