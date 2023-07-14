package socket_project_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import socket_project_client.DTO.RequestBodyDTO;

@RequiredArgsConstructor

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
				join(500);

			} catch (IOException e) {

				e.printStackTrace();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}// run 종료

	private void requestController(String requestBody) {
		Gson gson = new Gson();
		String resource = gson.fromJson(requestBody, RequestBodyDTO.class).getResource();

		System.out.println(resource);
		switch (resource) {
		case "updateRoomList":
			List<String> roomList = (List) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
			System.out.println(roomList);
			ClientApp.getInstance().getRoomListModel().clear();
			ClientApp.getInstance().getRoomListModel().addAll(roomList);

			break;

		case "sendMessage":
			String messageContent = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
			ClientApp.getInstance().getMessageArea().append(messageContent + "\n");

			break;

		case " exitRoom":
			String responseType = (String) gson.fromJson(requestBody, RequestBodyDTO.class).getBody();
			ClientApp.getInstance().getMessageArea().setText("");
			ClientApp.getInstance().getMainCardLayout().show(ClientApp.getInstance().getMainCardPanel(),
					"roomListPanel");
			break;

		default:
			break;
		}// switch 종료
	}// requestController 종료
} // 클래스 종료
