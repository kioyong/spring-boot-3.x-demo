package com.yong.boot.config;

import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

@EnableAsync
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    @Bean(name = "threadPoolTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {

            @Override
            public void execute(Runnable task) {
                super.execute(mdcWrapper(task));
            }

            @Override
            public void execute(Runnable task, long startTimeout) {
                super.execute(mdcWrapper(task), startTimeout);
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(mdcWrapper(task));
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(() -> {
                    try {
                        MDC.setContextMap(MDC.getCopyOfContextMap());
                        return task.call();
                    } finally {
                        MDC.clear();
                    }
                });
            }

        };
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(500);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("myExecutor-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    Runnable mdcWrapper(Runnable r) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            if (map != null) {
                MDC.setContextMap(map);
            }
            try {
                r.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
