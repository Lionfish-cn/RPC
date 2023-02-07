package github.common.dto;


import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcMessage {
    private int requestId;
    private Object data;
}
