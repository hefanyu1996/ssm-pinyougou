package cn.itcast.service.impl;

import cn.itcast.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

public class ItemSearchDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage) message;
        try {

            Long[] ids = (Long[]) objectMessage.getObject();

            itemSearchService.deleteSolrSku(Arrays.asList(ids));

            System.out.println("已同步删除索引库商品");

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
