package github.lionfish.test;

import github.lionfish.annotation.RpcScan;
import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.transport.server.NettyRpcServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@RpcScan(basePackage = {"github.lionfish"})
public class ServerMain {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerMain.class);

        NettyRpcServer nettyRpcServer = (NettyRpcServer) context.getBean("nettyRpcServer");
        Object[] params = new Object[2];
        params[0] = "123";
        params[1] = 312;

        RpcRequest request = RpcRequest.builder().interfaceName(HelloService.class.getCanonicalName()).
                methodName("hello").params(params).requestId(123).weight(3).build();
        nettyRpcServer.registryService(request);
        nettyRpcServer.start();

    }
}
