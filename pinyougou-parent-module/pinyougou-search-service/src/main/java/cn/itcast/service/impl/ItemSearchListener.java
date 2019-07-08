package cn.itcast.service.impl;

import cn.itcast.pojo.TbItem;
import cn.itcast.service.ItemSearchService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;

        try {
            //接收消息
            String text = textMessage.getText();
            //解析为List
            List<TbItem> skuList = JSON.parseArray(text,TbItem.class);
            //同步solr索引库
            itemSearchService.importList(skuList);
            System.out.println("skuList成功导入索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
