package github.rpcclient.call;

import github.common.codec.RpcDecoder;
import github.common.codec.RpcEncoder;
import github.common.dto.RpcMessage;
import github.common.dto.RpcRequest;
import github.common.dto.RpcResponse;
import github.common.factory.SingletonFactory;
import github.rpcclient.service.ServiceHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient {

    private final ChannelProvider channelProvider;

    ServiceHandler serviceHandler = SingletonFactory.getInstance(ServiceHandler.class);

    static Bootstrap b;

    public NettyRpcClient() {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(loopGroup).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new NettyRpcClientHandler());
                    }
                });
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    public RpcResponse sendRpcRequest(RpcRequest rpcRequest) {
        try {
            String path = "/lionfish/server/RPC_SERVER";
            InetSocketAddress inetSocketAddress = serviceHandler.lookupService(path);

            Channel channel = getChannel(inetSocketAddress);

            RpcMessage rpcMessage = RpcMessage.builder().requestId(rpcRequest.getRequestId()).data(rpcRequest).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message:[{}]", rpcRequest.toString());
                } else {
                    future.cause().printStackTrace();
                }
            });

            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            return channel.attr(key).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {

        Channel channel = channelProvider.get(inetSocketAddress);
        try {
            if (channel == null) {
                ChannelFuture channelFuture = b.connect(inetSocketAddress).sync();
                channel = channelFuture.channel();
                channelProvider.set(inetSocketAddress, channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return channel;
    }

}
