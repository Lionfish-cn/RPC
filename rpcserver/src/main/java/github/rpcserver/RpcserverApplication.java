package github.rpcserver;

import github.rpcserver.receive.NettyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcserverApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcserverApplication.class, args);
    }

    @Autowired
    NettyRpcServer nettyRpcServer;

    @Override
    public void run(String... args) throws Exception {
        nettyRpcServer.serviceRegistry("RPC_SERVER");
        nettyRpcServer.run();
    }
}
