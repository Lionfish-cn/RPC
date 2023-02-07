package github.rpcserver.service;

import github.common.dto.RpcRequest;
import github.common.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ServiceHandler {

    List<Class<?>> classes = new CopyOnWriteArrayList<>();

    public ServiceHandler() {
        this.initAnnotationRelation();
    }

    public void initAnnotationRelation() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath("github.rpcserver") + "/**/*Controller.class";

        try {
            Resource[] resources = resolver.getResources(pattern);
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);
            for (Resource resource : resources) {
                MetadataReader metadataReader = factory.getMetadataReader(resource);
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                String className = classMetadata.getClassName();
                Class<?> aClass = Class.forName(className);
                RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
                if (annotation != null) {
                    classes.add(aClass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object handler(RpcRequest o) {//映射接口数据
        try {
            String interfaceName = o.getInterfaceName();
            if (o.getType() == 1) {
                try {
                    for (Class<?> v : classes) {
                        RequestMapping annotation = v.getAnnotation(RequestMapping.class);
                        String value = annotation.value()[0];
                        if (interfaceName.equals(value)) {
                            Method[] methods = v.getMethods();
                            for (Method method : methods) {
                                RequestMapping anno = method.getAnnotation(RequestMapping.class);
                                String s = anno.value()[0];
                                if (s.equals(o.getMethod())) {
                                    return method.invoke(v.newInstance(), o.getParameters());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Class<?> aClass = Class.forName(interfaceName);
                Method method = aClass.getMethod(o.getMethod(), Object[].class);
                return method.invoke(o, o.getParameters());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void registry(String service, InetSocketAddress inetSocketAddress) {

        String path = "/lionfish/server/" + service + inetSocketAddress.toString();
        boolean is = CuratorUtils.createPersistentNode(path);
        if (is) {
            log.info("service [{}] registered successfully", service);
        } else {
            log.error("service [{}] registered failed", service);
        }

    }


}
