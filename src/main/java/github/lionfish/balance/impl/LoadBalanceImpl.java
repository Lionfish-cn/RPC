package github.lionfish.balance.impl;

import github.lionfish.balance.LoadBalance;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public abstract class LoadBalanceImpl implements LoadBalance {

    @Override
    public String selectServiceUrl(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return "";
        }
        if (urls.size() == 1) {
            return urls.get(0);
        }
        return doSelect(urls);
    }

    protected abstract String doSelect(List<String> urls);
}
