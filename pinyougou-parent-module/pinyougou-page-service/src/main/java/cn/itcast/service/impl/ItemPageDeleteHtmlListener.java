package cn.itcast.service.impl;

import cn.itcast.service.ItemPageService;
import com.alibaba.druid.filter.AutoLoad;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.io.Serializable;

public class ItemPageDeleteHtmlListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage = (ObjectMessage) message;

        try {

            Long[] ids = (Long[]) objectMessage.getObject();

            for (Long id : ids) {
                System.out.println(id);
                boolean flag = itemPageService.removeItemHtml(id);

                System.out.println("同步删除商品详情页"+id+".html ："+flag);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
