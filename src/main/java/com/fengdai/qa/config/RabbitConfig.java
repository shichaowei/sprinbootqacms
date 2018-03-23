package com.fengdai.qa.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {

    @Bean
    public Queue helloMonitor() {
        return new Queue("monitor");
    }

    @Bean
    public Queue neoQueue() {
        return new Queue("brandon");
    }



}
