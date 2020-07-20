package com.carpoolling.configurations;

import com.carpoolling.controllers.CarPoolingCrtl;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author acard0s0
 * 
 *  This class is use to configure the asynchronization process that are define 
 * in the JourneyService class.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {
    
    private Logger logger = LoggerFactory.getLogger(CarPoolingCrtl.class);
    
    @Bean(name="taskExecutor")
    public Executor taskExecutor() {
        
        logger.debug("Creating Async Task Executor");
        
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("CarAssigmentThread-");
        executor.initialize();
        
        return executor;
    }
}
