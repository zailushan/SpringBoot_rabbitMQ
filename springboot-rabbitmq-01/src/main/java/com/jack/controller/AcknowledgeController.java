package com.jack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.dto.User;
import com.jack.response.BaseResponse;
import com.jack.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/1 7:13
 */
@RestController
public class AcknowledgeController {

    private static final Logger log= LoggerFactory.getLogger(AcknowledgeController.class);
    private static final String Prefix="ack";

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment env;

    @RequestMapping(value = Prefix+"/user/info",method = RequestMethod.GET)
    public BaseResponse ackUser() {
        try {
            User user = new User(1, "张三", "张");
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("simple.mq.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("simple.mq.routing.key.name"));
            Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(user)).build();
            rabbitTemplate.convertAndSend(msg);
            log.info("发送消息成功...");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new BaseResponse(StatusCode.Success);
    }
}
