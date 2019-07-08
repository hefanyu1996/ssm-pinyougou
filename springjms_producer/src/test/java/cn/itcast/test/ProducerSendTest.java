package cn.itcast.test;


import cn.itcast.demo.QueueProducer;
import cn.itcast.demo.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/application-jms-producer.xml")
public class ProducerSendTest {

    @Autowired
    private QueueProducer queueProducer;

    @Test
    public void test01(){

        queueProducer.sendTextMassage("spring整合activeMQ-点对点测试...");

    }

    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void test02(){

        topicProducer.sendTextMessage("spring整合activeMQ-发布订阅测试...");

    }



}
