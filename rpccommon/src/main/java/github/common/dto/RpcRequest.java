package github.common.dto;


import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RpcRequest implements Serializable {
    private final static long serialVersionUID = 198471948110999L;
    private int requestId;
    private String interfaceName;
    private String method;
    private Object[] parameters;
    private int type;
}
