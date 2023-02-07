package github.lionfish.remoting.dto;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private int requestId;
    private Object result;
}
