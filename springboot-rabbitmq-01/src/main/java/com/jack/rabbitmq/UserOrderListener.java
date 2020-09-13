package com.jack.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.dto.UserOrderDTO;
import com.jack.entity.UserOrder;
import com.jack.mapper.UserOrderMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/1 20:57
 */
@Component
public class UserOrderListener implements ChannelAwareMessageListener {

    private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] body = message.getBody();
            UserOrderDTO dto = objectMapper.readValue(body, UserOrderDTO.class);
            log.info("用户商城下单监听到消息： {} ",dto);

            UserOrder entity = new UserOrder();
            BeanUtils.copyProperties(dto, entity);
            entity.setStatus(1);
            userOrderMapper.insert(entity);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("用户商城下单 发生异常：",e.fillInStackTrace());
            channel.basicReject(deliveryTag,false);
        }
    }
}
