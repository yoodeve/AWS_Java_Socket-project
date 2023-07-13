package socket_project_client;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;

import socket_project_client.DTO.RequestBodyDTO;

public class ClientSender {
	private Gson gson;
	private static ClientSender instance;
	
	private ClientSender() {
		gson = new Gson();
	}

	public static ClientSender getInstance() {
		if (instance == null) {
			instance = new ClientSender();
		}
		return instance;
	}

	// 전송 메소드(재사용 가능)
	public void send(RequestBodyDTO<?> requestBodyDto) {
		try {
			System.out.println("clientSender");
			PrintWriter printWriter = new PrintWriter(ClientApp.getInstance().getSocket().getOutputStream(), true);
			printWriter.println(gson.toJson(requestBodyDto));
			System.out.println("clientSender"+gson.toJson(requestBodyDto));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
