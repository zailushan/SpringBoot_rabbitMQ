package com.jack.rabbitmq;

import com.jack.service.ConcurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/26 21:16
 */
@Component
public class RobbingListener {
    private static final Logger log= LoggerFactory.getLogger(RobbingListener.class);

    @Autowired
    private ConcurrencyService concurrencyService;

    @RabbitListener(queues = "${product.robbing.mq.queue.name}", containerFactory = "multiListenerContainer")
    public void consumeMessage(@Payload byte[] message) {
        try {
            String mobile = new String(message, "UTF-8");
            concurrencyService.manageRobbing(mobile);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
