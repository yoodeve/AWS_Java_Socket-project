package socket_project_server.DTO;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SendMessage {
	private String fromUsername;
	private String toUsername;
	private String messageBody;
}
