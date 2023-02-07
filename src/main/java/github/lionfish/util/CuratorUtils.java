package github.lionfish.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

public class CuratorUtils {


    private final static int BASE_SLEEP_TIME_MS = 3000;
    private final static int MAX_RETRIES = 3;

    public static CuratorFramework zkClient(){
        //重试策略，3秒重试3次
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);
        String hostAddress = "";
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            hostAddress = localHost.getHostAddress();

        }catch (Exception e){
            e.printStackTrace();
            hostAddress = "127.0.0.1";
        }

        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(hostAddress+":2181").
                retryPolicy(retry).
                build();
        client.start();
        return client;
    }


    public List<String> getNodeChildrens(String serviceName){



        return null;
    }



}
