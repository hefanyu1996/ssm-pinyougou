package cn.itcast.service.impl;

import cn.itcast.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * 生成商品详情页监听器
 */
public class ItemPageCreateHtmlListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage) message;
        try {

            Long id = (Long) objectMessage.getObject();

            boolean flag = itemPageService.genItemHtml(id);

            System.out.println("生成"+id+"商品详情页："+flag);

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
