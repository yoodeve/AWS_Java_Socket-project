package socket_project_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {
	
	public static List<ConnectedSocket> connectedSocketList = new ArrayList<>();
//	public static List<Room> roomList = new ArrayList<>();
	
	
	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(8000);
			System.out.println("[[ === 서버 실행 === ]]");
			while(true) {
				Socket socket = serverSocket.accept();
				ConnectedSocket	connectedSocket = new ConnectedSocket(socket); 
				connectedSocket.start();
				connectedSocketList.add(connectedSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
