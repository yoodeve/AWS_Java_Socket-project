package socket_project_client;

import com.google.gson.Gson;

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
				break;
				
			default:
				break;
		}// switch 종료
	}// requestController 종료
} // 클래스 종료
