package github.lionfish.balance.impl;



import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class WeightRandomLoadBalance extends LoadBalanceImpl {

    public List<String> roundRobinWeight(List<String> urls){
        List<String> lists = new CopyOnWriteArrayList<>();
        for (String url : urls) {
            String weight = url.split(":")[2];//权重

            int w = Integer.parseInt(weight);
            for(int i=0;i<w;i++){//依据权重放置不同多的地址
                lists.add(url);
            }
        }

        return lists;
    }

    @Override
    protected String doSelect(List<String> urls) {
        List<String> hosts = roundRobinWeight(urls);
        int i = ThreadLocalRandom.current().nextInt(hosts.size());
        return hosts.get(i);
    }
}
