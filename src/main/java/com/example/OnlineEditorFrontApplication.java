package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* @author wuyuefeng
* @brief springBoot 启动器
* @email 565948592@qq.com
* @date 2020-03-27
*/
@SpringBootApplication
@EnableSwagger2
public class OnlineEditorFrontApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineEditorFrontApplication.class, args);
    }
}
