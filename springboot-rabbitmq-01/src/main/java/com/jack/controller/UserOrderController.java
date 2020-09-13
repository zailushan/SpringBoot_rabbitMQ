package com.jack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jack.dto.LogDTO;
import com.jack.dto.UserOrderDTO;
import com.jack.entity.UserOrder;
import com.jack.mapper.UserOrderMapper;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/1 21:17
 */
@RestController
public class UserOrderController {

    Logger log = LoggerFactory.getLogger(UserOrderController.class);
    private static final String Prefix="user/order";
    @Autowired
    private Environment  env;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserOrderMapper userOrderMapper;

    @RequestMapping(value = Prefix + "/push", method = RequestMethod.POST)
    public BaseResponse pushUserOrder(@RequestBody UserOrderDTO dto) {
        BaseResponse response=new BaseResponse(StatusCode.Success);
        log.debug("接收到数据： {} ",dto);
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("user.order.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("user.order.routing.key.name"));
            Message msg = MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).build();
            rabbitTemplate.send(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //TODO: 用户下单时记录日志，系统级别-日志记录-异步处理
        try {
            LogDTO logDTO = new LogDTO("pushUserOrder", objectMapper.writeValueAsString(dto));
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("log.system.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("log.system.routing.key.name"));
            rabbitTemplate.convertAndSend(logDTO, new MessagePostProcessor(){
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, LogDTO.class);
                    return message;
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 用户商城下单
     * @param dto
     * @return
     */
    @RequestMapping(value = Prefix+"/push/dead/queue",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse pushUserOrderV2(@RequestBody UserOrderDTO dto){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        UserOrder userOrder=new UserOrder();
        try {
            BeanUtils.copyProperties(dto,userOrder);
            userOrder.setStatus(1);
            userOrderMapper.insertSelective(userOrder);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Integer id=userOrder.getId();
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("user.order.dead.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("user.order.dead.routing.key.name"));
            rabbitTemplate.convertAndSend(id, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties=message.getMessageProperties();
                    properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,Integer.class);
                    return message;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 用户商城下单-动态TTL设置
     * @param dto
     * @return
     */
    @RequestMapping(value = Prefix+"/push/dead/queue/v3",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse pushUserOrderV3(@RequestBody UserOrderDTO dto){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        UserOrder userOrder=new UserOrder();
        try {
            BeanUtils.copyProperties(dto,userOrder);
            userOrder.setStatus(1);
            userOrderMapper.insertSelective(userOrder);
            log.info("用户商城下单成功!!");
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Integer id=userOrder.getId();
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("dynamic.dead.produce.exchange.name"));
            rabbitTemplate.setRoutingKey(env.getProperty("dynamic.dead.produce.routing.key.name"));

            Long ttl=15000L; //可以用随机数替代
            rabbitTemplate.convertAndSend(id, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties properties=message.getMessageProperties();
                    properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    properties.setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,Integer.class);

                    properties.setExpiration(String.valueOf(ttl));
                    return message;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
