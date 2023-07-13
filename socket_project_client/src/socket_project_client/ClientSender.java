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
//		try {
			// 아래행 루트파일 싱글톤 작성 후 주석해제, 생성 후 getSocket
//			PrintWriter printWriter = new PrintWriter(ClientRoot.getSocket().getOutputStream(), true);
//			printWriter.println(gson.toJson(requestBodyDto));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
