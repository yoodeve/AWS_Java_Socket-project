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
	} // run 종료

	private void requestController(String requestBody) {
		String resource = gson.fromJson(requestBody, RequestBodyDTO.class).getResource();
		System.out.println("서버 리소스 " + resource);
		switch (resource) {

		// enter(나):join
		// 채
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
//
//		default:
//			break;
		} // 스위치 종료
	} // 컨트롤러 종료

	private void connection(String requestBody) {
		username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		List<String> roomNameList = new ArrayList<>();

		ServerApp.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});

		RequestBodyDTO<List<String>> updateRoomRequestBodyDTO = new RequestBodyDTO<List<String>>("updateRoomList",
				roomNameList);

		ServerSender.getInstance().send(socket, updateRoomRequestBodyDTO);

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
		System.out.println("reqBody" + requestBody);
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		ServerApp.roomList.forEach(connectedSocket -> {
			// 같은 방 찾기
			connectedSocket.getUserList().add(this);
			if (connectedSocket.getRoomName().equals(roomName)) {

				username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
				List<String> usernameList = new ArrayList<>();
				System.out.println("username !! " + username);
				// 방 동접 사람들
				System.out.println("getUserList" + connectedSocket.getUserList());

				connectedSocket.getUserList().forEach(con -> {
//					RequestBodyDTO<List<String>> updateUserListDto = new RequestBodyDTO<List<String>>("updateUserList",
//							usernameList);
					usernameList.add(con.username);
//					ServerSender.getInstance().send(con.socket, updateUserListDto);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					RequestBodyDTO<String> joinMessageDto = new RequestBodyDTO<String>("sendMessage",
							username + "님 입장~!");
					System.out.println("서버 입장메시지" + joinMessageDto);
					ServerSender.getInstance().send(socket, joinMessageDto);
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
			// 같은 방 찾기
			if (connectedSocket.getUserList().contains(this)) {
				connectedSocket.getUserList().add(this);
				System.out.println("json객체: " + gson.fromJson(requestBody, RequestBodyDTO.class));
				username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
				List<String> usernameList = new ArrayList<>();
				// 방 동접 사람들
				System.out.println("getUserList" + connectedSocket.getUserList());

				connectedSocket.getUserList().forEach(con -> {
					usernameList.add(con.username);
					RequestBodyDTO<String> dto = new RequestBodyDTO<>("sendMessage",
							sendMessage.getFromUsername() + " : " + sendMessage.getMessageBody());
					ServerSender.getInstance().send(con.socket, dto);
				});

			}
		});

	}

	private void exitRoom(String requestBody) {
		ServerApp.connectedSocketList.forEach(connectedSocket -> {
			// 같은 방 찾기
//			if (room.getRoomName().equals(roomName)) {
//				room.getUserList().add(this);
			username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
			System.out.println("username !! " + username);
//			List<String> usernameList = new ArrayList<>();
			// 방 동접 사람들
//				room.getUserList().forEach(con -> {
//					usernameList.add(con.username);
//				});
//				room.getUserList().forEach(connectedSocket -> {
//			RequestBodyDTO<List<String>> updateUserListDto = new RequestBodyDTO<List<String>>("updateUserList", usernameList);
			RequestBodyDTO<String> joinMessageDto = new RequestBodyDTO<String>("sendMessage", username + "님 퇴장~!");
//			ServerSender.getInstance().send(connectedSocket.socket, updateUserListDto);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			ServerSender.getInstance().send(connectedSocket.socket, joinMessageDto);
		});
	}
}

// 각종 메소드 위치
