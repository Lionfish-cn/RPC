package github.common.codec;

import github.common.codec.serialize.KryoSerializer;
import github.common.dto.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {

    KryoSerializer kryoSerializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage o, ByteBuf byteBuf) {
        try {
            byte[] serialize = kryoSerializer.serialize(o);
            int le = serialize.length;
            byteBuf.writeInt(le);
            byteBuf.writeBytes(serialize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
