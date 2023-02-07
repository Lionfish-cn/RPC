package github.lionfish.remoting.transport.client;


import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.dto.RpcResponse;
import github.lionfish.remoting.transport.RpcRequestTransport;
import github.lionfish.remoting.transport.codec.NettyRpcDecoder;
import github.lionfish.remoting.transport.codec.NettyRpcEncoder;
import github.lionfish.service.ServiceDiscovery;
import github.lionfish.test.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j

@Component
public class NettyRpcClient implements RpcRequestTransport {

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    static Bootstrap bootstrap;


    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_BACKLOG, 3)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyRpcEncoder());
                        ch.pipeline().addLast(new NettyRpcDecoder());
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public RpcResponse sendRpcRequest(RpcRequest rpcRequest) {

        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);

        try {
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message:[{}]", rpcRequest.toString());
                } else {
                    future.channel().closeFuture();
                }
            });

            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
            return channel.attr(key).get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Channel getChannel(InetSocketAddress inetSocketAddress) {

        ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress).addListener(future -> {
            if (future.isSuccess()) {
                log.info("client connection server is success");
            } else {
                throw new Exception("client connection server is failure");
            }
        });

        return channelFuture.channel();
    }

    public static void main(String[] args) {
        RpcRequest request = RpcRequest.builder().interfaceName(HelloService.class.getCanonicalName()).
                methodName("hello").requestId(123).weight(3).build();
        new NettyRpcClient().sendRpcRequest(request);
    }
}
