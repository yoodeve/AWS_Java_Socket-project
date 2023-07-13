package socket_project_server;

import java.net.Socket;

import com.google.gson.Gson;

public class ConnectedSocket extends Thread {
	private final Socket socket = null;
	private String username;
	private Gson gson;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
	} // run 종료
	
	private void requestController(String requestBody) {
		String resource = null;
		
		switch (resource) {
		case "showMessage" :
			break;
			
		default:
			break;
		} // 스위치 종료
	} // 컨트롤러 종료
	
	// 각종 메소드 위치
}
