package socket_project_server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import socket_project_server.DTO.RequestBodyDTO;

public class ServerSender {
	private Gson gson;
	private static ServerSender instance;

	private ServerSender() {
		gson = new Gson();
	}

	public static ServerSender getInstance() {
		if (instance == null) {
			instance = new ServerSender();
		}
		return instance;
	}

	// 전송 메소드(재사용 가능)
	public void send(Socket socket, RequestBodyDTO<?> requestBodyDto) {
		try {
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println(gson.toJson(requestBodyDto));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
