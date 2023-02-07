package github.common.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CuratorUtils {

    private final static Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private final static int BASE_SLEEP_TIME_MS = 3000;
    private final static int MAX_RETRIES = 3;

    public static CuratorFramework zkClient() {
        //重试策略，3秒重试3次
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);
        String hostAddress = "";
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            hostAddress = localHost.getHostAddress();

        } catch (Exception e) {
            e.printStackTrace();
            hostAddress = "127.0.0.1";
        }

        CuratorFramework client = CuratorFrameworkFactory.builder().
                connectString(hostAddress + ":2181").
                retryPolicy(retry).
                build();
        client.start();
        return client;
    }


    public static boolean createPersistentNode(String path) {
        CuratorFramework client = zkClient();
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(path);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteNode(String path){
        CuratorFramework client = zkClient();
        try{
            client.delete().deletingChildrenIfNeeded().forPath(path);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getNode(String path){
        try{
            CuratorFramework client = zkClient();
            if (client.checkExists().forPath(path) != null) {
                byte[] bytes = client.getData().forPath(path);
                return new String(bytes);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void setNode(String path){
        try{
            CuratorFramework client = zkClient();
            if (client.checkExists().forPath(path) != null) {
                client.setData().forPath(path,"123".getBytes());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static List<String> getNodeChildrens(String path) {
        if (SERVICE_ADDRESS_MAP.containsKey(path)) {
            return SERVICE_ADDRESS_MAP.get(path);
        }
        try {
            CuratorFramework client = zkClient();
            List<String> urls = client.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(path, urls);
            registerWatcher(path);
            return urls;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static void registerWatcher(String path) {

        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient(), path, true);
            PathChildrenCacheListener pathChildrenCacheListener = (client, cache) -> {
                List<String> urls = client.getChildren().forPath(path);
                SERVICE_ADDRESS_MAP.put(path, urls);
            };

            pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
            pathChildrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
