package github.rpcclient.service;

import github.common.factory.SingletonFactory;
import github.common.util.CuratorUtils;
import github.rpcclient.balance.LoadBalance;

import java.net.InetSocketAddress;
import java.util.List;

public class ServiceHandler {

    LoadBalance loadBalance = SingletonFactory.getInstance(LoadBalance.class);

    public InetSocketAddress lookupService(String path) {
        List<String> nodeChildrens = CuratorUtils.getNodeChildrens(path);

        String ip = loadBalance.obtainServerAddress(nodeChildrens);
        String[] ips = ip.split(":");

        return new InetSocketAddress(ips[0], Integer.parseInt(ips[1]));
    }
}
