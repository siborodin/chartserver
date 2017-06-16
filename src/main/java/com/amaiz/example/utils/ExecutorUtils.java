package com.amaiz.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorUtils {

    public static Executor newLoggableSingleThreadExecutor(String name) {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(new RunnableLoggable(r));
            t.setName(name);
            return t;
        });

    }

    private static class RunnableLoggable implements Runnable {
        private static final Logger log = LoggerFactory.getLogger(RunnableLoggable.class);
        Runnable runnable;

        public RunnableLoggable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } catch(Throwable t) {
                log.error("Uncaught throwable occurred", t);
                throw t;
            }
        }
    }

}
