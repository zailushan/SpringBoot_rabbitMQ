package com.jack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.entity.User;
import com.jack.entity.UserLog;
import com.jack.mapper.UserMapper;
import com.jack.response.BaseResponse;
import com.jack.response.StatusCode;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/6 16:50
 */
@RestController
public class UserController {

    Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String Prefix = "user";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    @RequestMapping(value = Prefix + "/login", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse login(@Param("userName") String userName, @Param("password") String password) {
        BaseResponse baseResponse = new BaseResponse(StatusCode.Success);
        try {
            User user = userMapper.selectByUserNamePassword(userName, password);
            if (user != null) {
                UserLog userLog = new UserLog(userName, "Login", "login", objectMapper.writeValueAsString(user));
                rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                rabbitTemplate.setExchange(env.getProperty("log.user.exchange.name"));
                rabbitTemplate.setRoutingKey(env.getProperty("log.user.routing.key.name"));

                //发送消息写法一
                // MessageProperties properties=new MessageProperties();
                // properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                // properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
                // Message message=new Message(objectMapper.writeValueAsBytes(userLog),properties);

                //发送消息写法二
                Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(userLog)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
                msg.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
                rabbitTemplate.convertAndSend(msg);

                System.out.println("已发送消息......");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return baseResponse;
    }

}
