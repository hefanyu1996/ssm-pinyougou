package cn.itcast.demo;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


public class MyMessageListener implements MessageListener {

    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;

        try {
            String text = textMessage.getText();

            System.out.println("接收到的消息："+text);

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
