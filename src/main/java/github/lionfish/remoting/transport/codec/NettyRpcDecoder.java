package github.lionfish.remoting.transport.codec;

import github.lionfish.serializer.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class NettyRpcDecoder extends ByteToMessageDecoder {

    KryoSerializer kryoSerializer;

    Class<?> genericClass;


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > 4) {
            byteBuf.markReaderIndex();//记录阅读索引
            int r = byteBuf.readInt();
            if (r < 1) {
                log.error("byte length is valid [{}]", r);
                return;
            }

            byte[] b = new byte[r];
            byteBuf.readBytes(b);

            Object d = kryoSerializer.deserialize(b, genericClass);
            list.add(d);
        }
    }
}
