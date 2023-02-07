package github.lionfish.remoting.transport.server;

import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.dto.RpcResponse;
import github.lionfish.remoting.handler.RpcHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private RpcHandler rpcHandler;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) msg;
            Object data = rpcHandler.handler(request);
            RpcResponse response = RpcResponse.builder().requestId(request.getRequestId()).result(data).build();
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
