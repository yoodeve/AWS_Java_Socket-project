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

		case "sendPrivateMessage":

			sendPrivateMessage(requestBody);

			break;

//
// default:
// break;
		} // 스위치 종료
	} // 컨트롤러 종료

	private void connection(String requestBody) {
		username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
		System.out.println("connection username" + username);
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
// 이 객체 빌드 시 userList가 들어가지 않아 생기는 문제
		Room newRoom = Room.builder().roomName(roomName).owner(username).userList(new ArrayList<ConnectedSocket>())
				.build();
		System.out.println(newRoom);
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
// 같은 방 찾기
			if (room.getRoomName().equals(roomName)) {
// this는 connectedSocket
				room.getUserList().add(this);

				List<String> usernameList = new ArrayList<>(); // 의심됨
// 방 동접 사람들
				room.getUserList().forEach(con -> {
					usernameList.add(con.username);
				});
// 동접사람들한테 유저리스트도 업데이트하고, 입장메세지도 띄우고
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDTO<List<String>> updateUserListDto = new RequestBodyDTO<List<String>>("updateUserList",
							usernameList);
					ServerSender.getInstance().send(connectedSocket.socket, updateUserListDto);
					RequestBodyDTO<String> joinMessageDto = new RequestBodyDTO<String>("sendMessage",
							username + "님 입장");
// 메세지 보내고
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
// 같은 방 찾기
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
				// room.getUserList().remove(this); // 방폭
				room.getUserList().removeIf(connectedSocket -> connectedSocket == this);

				List<String> usernameList = new ArrayList<>(); // 의심됨
				// 방 동접 사람들
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
							username + "님 퇴장~!");
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
				RequestBodyDTO<String> privateMessageDto = new RequestBodyDTO<>("receivePrivateMessage",
						privateMessageContent);
				ServerSender.getInstance().send(con.socket, privateMessageDto);
			}
		});
	}
}
