package github.lionfish.service;


import github.lionfish.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    InetSocketAddress lookupService(RpcRequest rpcRequest);


}
