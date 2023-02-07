package github.rpcclient;

import github.common.dto.RpcRequest;
import github.common.dto.RpcResponse;
import github.rpcclient.call.NettyRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcclientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RpcclientApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Object[] objs = new Object[2];
        objs[0] = "127.0.0.1";
        objs[1] = 1234;
        RpcRequest rpcRequest = RpcRequest.builder().interfaceName("test").method("hello").parameters(objs).type(1).requestId(1).build();
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        RpcResponse response = nettyRpcClient.sendRpcRequest(rpcRequest);
        System.out.println(response.getData());
    }
}
