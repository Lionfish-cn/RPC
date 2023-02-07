package github.rpcserver.receive;

import github.common.codec.RpcDecoder;
import github.common.codec.RpcEncoder;
import github.common.factory.SingletonFactory;
import github.rpcserver.service.ServiceHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NettyRpcServer {

    private final static int PORT = 8319;

    ServiceHandler serviceHandler = SingletonFactory.getInstance(ServiceHandler.class);

    public void serviceRegistry(String serviceName) {//服务注册
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            serviceHandler.registry(serviceName, new InetSocketAddress(hostAddress, PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {//启动netty服务
        ServerBootstrap b = new ServerBootstrap();

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new RpcDecoder());
                            pipeline.addLast(new RpcEncoder());
                            pipeline.addLast(new NettyRpcServerHandler());
                        }
                    });

            ChannelFuture channelFuture = b.bind(PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
