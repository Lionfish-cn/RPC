package github.lionfish;

import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.transport.server.NettyRpcServer;
import github.lionfish.test.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"github.lionfish"})
public class LionfishApplication {

    @Autowired
    static NettyRpcServer nettyRpcServer;

    public static void main(String[] args) {


        SpringApplication.run(LionfishApplication.class, args);
    }

}
