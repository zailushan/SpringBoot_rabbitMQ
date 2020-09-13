package com.jack.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.dto.User;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/31 21:36
 */
@Component
public class SimpleListener implements ChannelAwareMessageListener {
    private static final Logger log= LoggerFactory.getLogger(SimpleListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] body = message.getBody();
            User user = objectMapper.readValue(body, User.class);
            log.info("简单消息监听确认机制监听到消息： {} ",user);
            // 消费消息时，出现异常，没有返回ack给rabbitmq，则这个消息的状态会变为unacked
            // 等该程序与rabbitmq毒啊开连接时 这个unacked的消息会重入队列
            // int a = 1 / 0;
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("简单消息监听确认机制发生异常：",e.fillInStackTrace());

            channel.basicReject(deliveryTag,false);
        }
    }
}
