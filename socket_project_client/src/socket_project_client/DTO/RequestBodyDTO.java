package socket_project_client.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestBodyDTO<T> {
	private String resource;
	private T body;
}
