package com.amaiz.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ChartServer {

    private static final Logger logger = LoggerFactory.getLogger(ChartServer.class);

    public static void main(String[] args) {
        logger.info("Starting ChartServer application");
        logger.info("Initializing and starting Spring context");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ChartServerConfig.class);
        context.refresh();
        context.start();

        // let app threads run...
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ignored) {
        }
    }
}
