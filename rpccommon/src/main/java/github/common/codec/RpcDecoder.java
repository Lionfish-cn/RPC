package github.common.codec;

import github.common.codec.serialize.KryoSerializer;
import github.common.dto.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class RpcDecoder extends ByteToMessageDecoder {

    KryoSerializer kryoSerializer = new KryoSerializer();
    Class<?> genericClass = RpcMessage.class;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        try {
            if (byteBuf.readableBytes() > 4) {
                byteBuf.markReaderIndex();
                int le = byteBuf.readInt();
                if (le < 1) {
                    log.error("Rpc request is valid");
                    return;
                }

                byte[] d = new byte[le];
                byteBuf.readBytes(d);


                Object data = kryoSerializer.deserialize(d, genericClass);
                list.add(data);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
