package socket_project_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import socket_project_server.DTO.RequestBodyDTO;
import socket_project_server.DTO.SendMessage;

@RequiredArgsConstructor
public class ConnectedSocket extends Thread {
	private final Socket socket;
	private String username;
	private Gson gson;

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
		System.out.println("server controller flag" + resource);
		switch (resource) {
		case "sendMessage":
			sendMessage(requestBody);
			break;

		case "enter":
			enter(requestBody);
			break;

		default:
			break;
		} // 스위치 종료
	} // 컨트롤러 종료

	private void sendMessage(String requestBody) {
		TypeToken<RequestBodyDTO<SendMessage>> typeToken = new TypeToken<RequestBodyDTO<SendMessage>>() {
		};
		RequestBodyDTO<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
		SendMessage sendMessage = requestBodyDto.getBody();
		ServerApp.connectedSocketList.forEach(con -> {
			RequestBodyDTO<String> dto = new RequestBodyDTO<String>("sendMessage",
					sendMessage.getFromUsername() + ": " + sendMessage.getMessageBody());
			ServerSender.getInstance().send(con.socket, dto);
		});

	}

	private void enter(String requestBody) {
		ServerApp.connectedSocketList.forEach(connectedSocket -> {
			username = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
			RequestBodyDTO<String> joinMessageDto = new RequestBodyDTO<String>("sendMessage",
					username + "님 입장~~~~~!!!");
			ServerSender.getInstance().send(connectedSocket.socket, joinMessageDto);
		});

//			}
//		});
	}
}