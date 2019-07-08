package cn.itcast.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * 消息生产者类
 */

@Component
public class QueueProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination queueTextDestination;

    /**
     * 发送文本消息
     * @param text
     */
    public void sendTextMassage(final String text){

        jmsTemplate.send(queueTextDestination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {

                return session.createTextMessage(text);

            }

        });

    }

}
