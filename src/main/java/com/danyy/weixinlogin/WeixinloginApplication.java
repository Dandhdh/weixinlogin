package com.danyy.weixinlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.danyy.controller")//包名
public class WeixinloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeixinloginApplication.class, args);
	}
}
