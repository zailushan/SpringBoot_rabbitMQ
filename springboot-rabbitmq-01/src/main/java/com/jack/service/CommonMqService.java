package com.jack.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/26 20:59
 */
@Service
public class CommonMqService {
    private static final Logger log = LoggerFactory.getLogger(CommonMqService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment environment;
    @Autowired
    private MailService mailService;

    /**
     * 发送抢单信息到消息队列
     * @param mobile
     */
    public void sendRobbingMsg(String mobile) {
        try {
            rabbitTemplate.setExchange(environment.getProperty("product.robbing.mq.exchange.name"));
            rabbitTemplate.setRoutingKey(environment.getProperty("product.robbing.mq.routing.key.name"));
            Message msg = MessageBuilder.withBody(mobile.getBytes("UTF-8")).build();
            rabbitTemplate.send(msg);
            log.info("客户手机号码:{},正在抢单中...", mobile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听消费邮件发送
     * @param message
     */
    @RabbitListener(queues = "${mail.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeMailQueue(@Payload byte[] message){
        try {
            log.info("监听消费邮件发送 监听到消息： {} ",new String(message,"UTF-8"));

            mailService.sendEmail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 监听消费死信队列中的消息
     * @param message
     */
    @RabbitListener(queues = "${simple.dead.real.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeDeadQueue(@Payload byte[] message){
        try {
            log.info("监听消费死信队列中的消息： {} ",new String(message,"UTF-8"));
            mailService.sendEmail();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
