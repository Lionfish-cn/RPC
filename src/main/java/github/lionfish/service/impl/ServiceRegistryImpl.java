package github.lionfish.service.impl;

import github.lionfish.remoting.constants.RpcConstants;
import github.lionfish.service.ServiceRegistry;
import github.lionfish.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;


@Slf4j
@Service
public class ServiceRegistryImpl implements ServiceRegistry {
    String RPC_SERVER = RpcConstants.RPC_SERVER;

    @Override
    public void serviceRegistry(String serviceName, InetSocketAddress inetSocketAddress) {
        CuratorFramework client = CuratorUtils.zkClient();

        String path = RPC_SERVER + "/" + serviceName + "/" + inetSocketAddress.toString();

        try {
            if (client.checkExists().forPath(path) != null) {
                log.warn("path [{}] is exist", path);
            } else {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
