package github.lionfish.remoting.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcProperties {

    @Value("${rpc.server.name}")
    private static String RPC_SERVER;

    @Value("${rpc.client.name}")
    private static String RPC_CLIENT;
}
