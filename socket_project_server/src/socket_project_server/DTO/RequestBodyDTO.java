package socket_project_server.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestBodyDTO<T> {
	// 처리내용 == resource(접속, 메세지 등..)
	private String resource;
	private T body;
}
