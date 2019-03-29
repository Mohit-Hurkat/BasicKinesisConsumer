package com.kinesis.server;

import com.kinesis.consumer.KinesisConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


/**
 * Created by mohit.hurkat.
 */

@ImportResource({"classpath*:applicationContext.xml"})
@SpringBootApplication
public class Server {

    private static ApplicationContext applicationContext;
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        init(args);
    }

    private static void init(String[] args) {
        try {
            // Starting Spring boot Application....
            applicationContext = SpringApplication.run(Server.class, args);
            System.out.println("Server Start");
            for (String name : applicationContext.getBeanDefinitionNames()) {
                logger.info("Bean Name : " + name);
            }
            logger.info("server started\n");
            new KinesisConsumer();
            logger.info(LoggerPattern.MPattern().toString());
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
