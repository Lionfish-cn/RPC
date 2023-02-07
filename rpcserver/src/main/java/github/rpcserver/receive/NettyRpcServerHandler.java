package github.rpcserver.receive;

import github.common.dto.RpcMessage;
import github.common.dto.RpcRequest;
import github.common.dto.RpcResponse;
import github.common.factory.SingletonFactory;
import github.rpcserver.service.ServiceHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final Log log = LogFactory.getLog(NettyRpcServerHandler.class);

    ServiceHandler serviceHandler = SingletonFactory.getInstance(ServiceHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        try {
            log.info("进入channelRead");
            if (msg instanceof RpcMessage) {
                RpcMessage rpcMessage = (RpcMessage) msg;
                RpcRequest rpcRequest = (RpcRequest) rpcMessage.getData();
                Object data = serviceHandler.handler(rpcRequest);
                RpcResponse response = RpcResponse.builder().data(data).requestId(rpcRequest.getRequestId()).build();
                rpcMessage.setData(response);
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("send massage is failure , error is [{}]", cause);
    }
}
