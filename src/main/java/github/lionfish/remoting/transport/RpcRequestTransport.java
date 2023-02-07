package github.lionfish.remoting.transport;

import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.dto.RpcResponse;

public interface RpcRequestTransport {
    RpcResponse sendRpcRequest(RpcRequest rpcRequest);
}
