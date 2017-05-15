package com.xebia.exercice6;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.xebia.MessageApi;

/**
 * The goal here is to use Hystrix semaphore isolation feature.
 * Semaphore isolation allows to limit concurrent calls to MessageApi.
 * Commands are executed on current Thread but rejected if semaphore has no more capacity.
 */
public class MessageClientWithSemaphore {

    private final MessageApi messageApi;

    private final HystrixCommand.Setter setter;


    public MessageClientWithSemaphore(MessageApi messageApi) {
        this.messageApi = messageApi;

        /*
         TODO:
         Create a Setter instance for an HystrixCommand and configure it to:
         - execute commands using a semaphore strategy
         - allow 2 max concurrent command executions
         */

        this.setter = HystrixCommand.Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupKey"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("CommandKey"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(2)
            );
    }

    public String getMessage(String userId) {

        // TODO create and execute an Hystrix Command with this setter in parameter

        return new HystrixCommand<String>(setter) {

            @Override
            protected String run() throws Exception {
                return messageApi.getMessage(userId);
            }

            @Override
            protected String getFallback() {
                return userId + " messages not available";
            }

        }.execute();

    }

}