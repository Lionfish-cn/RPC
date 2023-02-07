package github.lionfish.service.impl;

import github.lionfish.balance.LoadBalance;
import github.lionfish.remoting.constants.RpcConstants;
import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.service.ServiceDiscovery;
import github.lionfish.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

@Service
public class ServiceDiscoveryImpl implements ServiceDiscovery {

    String RPC_SERVER = RpcConstants.RPC_SERVER;

    @Autowired
    private LoadBalance loadBalance;

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getServiceName();
        CuratorFramework client = CuratorUtils.zkClient();
        try {

            List<String> serviceUrls = client.getChildren().forPath(RPC_SERVER + "/" + serviceName);

            String url = loadBalance.selectServiceUrl(serviceUrls);//负载均衡

            String[] hosts = url.split(":");
            String host = hosts[0];
            int port = Integer.parseInt(hosts[1]);

            return new InetSocketAddress(host, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
