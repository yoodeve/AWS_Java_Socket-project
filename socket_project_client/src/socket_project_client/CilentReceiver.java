package socket_project_client;

import com.google.gson.Gson;

import socket_project_client.DTO.RequestBodyDTO;

public class CilentReceiver extends Thread {
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	}// run 종료
	
	private void requestController(String requestBody) {
		Gson gson = new Gson();
		String resource = null;
		
		switch(resource) {
			case "showMessage":
				String messageContent = (String) gson.fromJson(requestBody,RequestBodyDTO.class).getBody();
				ClientApp.getInstance().getMessageArea().append(messageContent+"\n");
				break;
				
			default:
				break;
		}// switch 종료
	}// requestController 종료
} // 클래스 종료
