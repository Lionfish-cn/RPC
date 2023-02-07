package github.lionfish.remoting.handler;

import github.lionfish.remoting.dto.RpcRequest;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
public class RpcHandler {

    public Object handler(RpcRequest rpcRequest) {
        return invokeMethod(rpcRequest);
    }

    private Object invokeMethod(RpcRequest rpcRequest) {
        try {
            Class<?> aClass = Class.forName(rpcRequest.getInterfaceName());
            Method method = aClass.getMethod(rpcRequest.getMethodName(), Object[].class);
            return method.invoke(aClass, rpcRequest.getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
