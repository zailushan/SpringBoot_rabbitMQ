package com.jack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.jack.dto.User;
import com.jack.response.BaseResponse;
import com.jack.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/16 20:45
 */
@RestController
public class RabbitController {
    private static final Logger log= LoggerFactory.getLogger(RabbitController.class);
    private static final String Prefix="rabbit";
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * @Author caowenlong
     * @Description //发送简单消息
     * @Date 20:56 2020/8/16
     **/
    @RequestMapping(value = Prefix+"/simple/message/send",method = RequestMethod.GET)
    public BaseResponse sendSimpleMessaage(@RequestParam String message) {
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            log.info("待发送的消息： {} ",message);

            // 设置消息转换器
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("basic.info.mq.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("basic.info.mq.routing.key.name"));
            Message msg = MessageBuilder.withBody(message.getBytes("UTF-8")).build();
            rabbitTemplate.send(msg);
            // 使用消息转换器将消息进行转换并发送
            //rabbitTemplate.convertAndSend(msg);
        } catch (UnsupportedEncodingException e) {
            log.error("发送简单消息发生异常： ",e.fillInStackTrace());
        }
        return response;
    }

    @RequestMapping(value = Prefix+"/object/message/send",method = RequestMethod.POST)
    public BaseResponse sendObjectMessage(@RequestBody User user) {
        BaseResponse response=new BaseResponse(StatusCode.Success);
        log.info("待发送消息:{}", user);
        try {
            rabbitTemplate.setExchange(env.getProperty("basic.info.mq.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("basic.info.mq.routing.key.name"));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.convertAndSend(user, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties=message.getMessageProperties();
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,User.class);
                    return message;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 发送多类型字段消息
     * @return
     */
    @RequestMapping(value = Prefix+"/multi/type/message/send",method = RequestMethod.GET)
    public BaseResponse sendMultiTypeMessage(){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            Integer id=120;
            String name="阿修罗";
            Long longId=12000L;
            Map<String,Object> dataMap= Maps.newHashMap();

            dataMap.put("id",id);
            dataMap.put("name",name);
            dataMap.put("longId",longId);
            log.info("待发送的消息： {} ",dataMap);

            rabbitTemplate.setExchange(env.getProperty("basic.info.mq.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("basic.info.mq.routing.key.name"));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

            Message msg=MessageBuilder.withBody(objectMapper.writeValueAsBytes(dataMap)).setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
                    .build();
            rabbitTemplate.convertAndSend(msg);
        }catch (Exception e){
            log.error("发送多类型字段消息发生异常： ",e.fillInStackTrace());
        }
        return response;
    }

}
