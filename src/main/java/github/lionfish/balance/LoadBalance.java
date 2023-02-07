package github.lionfish.balance;

import java.util.List;

public interface LoadBalance {
    String selectServiceUrl(List<String> urls);
}
