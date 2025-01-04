package com.tsh.slt;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.sql.DataSource;

//@EnableEurekaClient
@EnableAsync
@SpringBootApplication
public class SltAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SltAgentApplication.class, args);
    }



}
