package com.jack.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.entity.UserLog;
import com.jack.mapper.UserLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/6 16:48
 */
@Component
public class CommonMqListener {
    private static final Logger log = LoggerFactory.getLogger(CommonMqListener.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserLogMapper userLogMapper;

    @RabbitListener(queues = "${log.user.queue.name}", containerFactory = "singleListenerContainer")
    public void consumeLogUserMessage(@Payload byte[] message) {
        try {
            UserLog userLog = objectMapper.readValue(message, UserLog.class);
            userLog.setCreateTime(new Date());
            log.info("已接受到消息:{}", userLog);
            userLogMapper.insert(userLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
