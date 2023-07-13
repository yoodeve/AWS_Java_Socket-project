package socket_project_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.Gson;

import socket_project_client.DTO.RequestBodyDTO;

public class CilentReceiver extends Thread {
	@Override
	public void run() {
		ClientApp app = ClientApp.getInstance();

		while (true) {
			try {
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(app.getSocket().getInputStream()));
				String requestBody = bufferedReader.readLine();
				requestController(requestBody);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}// run 종료

	private void requestController(String requestBody) {
		Gson gson = new Gson();
		String resource = gson.fromJson(requestBody, RequestBodyDTO.class).getResource();
		System.out.println("클라이언트 리소스====>" + resource);
		switch (resource) {
		case "sendMessage":
			String messageContent = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
			System.out.println(ClientApp.getInstance().getMessageArea() + "<<<===getMessageArea");
			ClientApp.getInstance().getMessageArea().append(messageContent + "\n");
			break;

		default:
			break;
		}// switch 종료
	}// requestController 종료
} // 클래스 종료
