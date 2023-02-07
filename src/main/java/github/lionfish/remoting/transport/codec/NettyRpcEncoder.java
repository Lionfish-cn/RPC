package github.lionfish.remoting.transport.codec;

import github.lionfish.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyRpcEncoder extends MessageToByteEncoder<Object> {

    KryoSerializer kryoSerializer;
    Class<?> genericClass;


    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)){
            byte[] serialize = kryoSerializer.serialize(o);
            int le = serialize.length;
            byteBuf.writeInt(le);
            byteBuf.writeBytes(serialize);
        }
    }
}
