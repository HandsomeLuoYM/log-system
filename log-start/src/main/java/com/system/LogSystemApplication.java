package com.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Ming
 * @date 2021/9/23 - 15:37
 */
@SpringBootApplication(scanBasePackages = "com")
@MapperScan("com.system.dao")
public class LogSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogSystemApplication.class, args);
    }
}
