package com.jack.rabbitmq;

import com.jack.dto.LogDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/2 21:34
 */
@Component
public class LogSystemListener {
    Logger log = LoggerFactory.getLogger(LogSystemListener.class);

    @RabbitListener(queues = "${log.system.queue.name}", containerFactory = "multiListenerContainer")
    public void consumeMessage(@Payload LogDTO logDTO) {
        try {
            log.info("系统日志监听器监听监听到消息: {} ",logDTO);
        }catch (Exception e){
            log.error("系统日志监听器监听发生异常：{} ",logDTO,e.fillInStackTrace());
        }
    }
}
