package github.lionfish.remoting.dto;

import lombok.*;

@Builder
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    private String interfaceName;
    private String methodName;
    private int weight;//权重
    private int requestId;
    private Object[] params;

    public String getServiceName(){
        return requestId + interfaceName + methodName;
    }
}
