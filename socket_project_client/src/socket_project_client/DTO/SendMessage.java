package socket_project_client.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class SendMessage {
	private String fromUsername;
	private String toUsername;
	private String messageBody;

}
