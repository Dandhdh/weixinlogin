package com.danyy.controller;
  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  
  
@RestController  
public class HelloController {  
  
    @RequestMapping("/hello")  
    public String hello() {
        return "hello,this is a springboot demo";
    }  
} 