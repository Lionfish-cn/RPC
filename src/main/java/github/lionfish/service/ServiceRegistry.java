package github.lionfish.service;

import java.net.Inet4Address;
import java.net.InetSocketAddress;

public interface ServiceRegistry {

    void serviceRegistry(String serviceName, InetSocketAddress inetSocketAddress);
}
