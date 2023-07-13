package socket_project_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8000);
			System.out.println("[[ === 서버 실행 === ]]");
			while(true) {
				Socket socket = serverSocket.accept();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
