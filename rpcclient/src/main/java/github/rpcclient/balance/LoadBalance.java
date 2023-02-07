package github.rpcclient.balance;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class LoadBalance {

    /**
     * 权加权轮询
     */
    public List<String> roundRobinWeight(List<String> urls) {
        List<String> ips = new ArrayList<>();
        for (String url : urls) {
            String[] paths = url.split(":");
            int weight = 1;
            if (paths.length >= 3) {
                weight = Integer.parseInt(paths[2]);
            }
            for (int i = 0; i < weight; i++) {
                ips.add(paths[0] + ":" + paths[1]);
            }
        }
        Collections.shuffle(ips);
        return ips;
    }


    public String obtainServerAddress(List<String> paths) {
        List<String> ips = roundRobinWeight(paths);
        int i = ThreadLocalRandom.current().nextInt(ips.size());
        return ips.get(i);
    }


}
