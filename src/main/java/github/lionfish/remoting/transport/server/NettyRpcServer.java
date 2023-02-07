package github.lionfish.remoting.transport.server;

import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.transport.codec.NettyRpcDecoder;
import github.lionfish.remoting.transport.codec.NettyRpcEncoder;
import github.lionfish.service.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Service
public class NettyRpcServer {

    public static final int PORT = 8319;
    static ServerBootstrap b;

    @Autowired
    ServiceRegistry serviceRegistry;

    public void registryService(RpcRequest rpcRequest) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, PORT);
            serviceRegistry.serviceRegistry(rpcRequest.getServiceName(), inetSocketAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new NettyRpcDecoder());
                            pipeline.addLast(new NettyRpcEncoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            String host = Inet4Address.getLocalHost().getHostAddress();
            ChannelFuture future = b.bind(host, PORT).sync();
            future.channel().closeFuture();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
