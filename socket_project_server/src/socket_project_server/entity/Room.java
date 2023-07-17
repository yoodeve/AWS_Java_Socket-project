package socket_project_server.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import socket_project_server.ConnectedSocket;

@Builder
@Data

public class Room {
	private String roomName;
	private String owner;
	private List<ConnectedSocket> userList;
}
