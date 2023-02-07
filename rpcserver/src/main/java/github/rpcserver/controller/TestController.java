package github.rpcserver.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("test")
public class TestController {
    @RequestMapping("hello")
    public String hello(String host,int port){
        return host+":"+port;
    }
}
