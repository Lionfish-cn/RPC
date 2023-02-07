package github.lionfish.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.lionfish.remoting.dto.RpcRequest;
import github.lionfish.remoting.dto.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer {

    ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {

        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;

    });

    public byte[] serialize(Object o) {
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream();
             Output output = new Output(bao)) {
            Kryo kryo = threadLocal.get();
            kryo.writeObject(output, o);

            threadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T deserialize(byte[] b, Class<T> t) {
        try (ByteArrayInputStream bai = new ByteArrayInputStream(b);
             Input input = new Input(bai)) {

            Kryo kryo = threadLocal.get();
            Object o = kryo.readObject(input, t);

            threadLocal.remove();
            return t.cast(o);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
