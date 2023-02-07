package github.common.codec.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.common.dto.RpcMessage;
import github.common.dto.RpcRequest;
import github.common.dto.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer {

    final ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcMessage.class);
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.register(Object[].class);
        return kryo;
    });

    public byte[] serialize(Object o) {
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
             Output output = new Output(bao);) {

            Kryo kryo = threadLocal.get();
            kryo.writeObject(output, o);

            threadLocal.remove();

            return output.toBytes();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public <T> T deserialize(byte[] d, Class<T> cls) {
        try (ByteArrayInputStream bai = new ByteArrayInputStream(d);
             Input input = new Input(bai)) {

            Kryo kryo = threadLocal.get();
            Object o = kryo.readObject(input, cls);

            threadLocal.remove();
            return cls.cast(o);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
