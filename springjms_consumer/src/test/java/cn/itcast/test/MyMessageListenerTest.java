package cn.itcast.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-jms-consumer-topic.xml")
public class MyMessageListenerTest {


    @Test
    public void test01(){

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test02(){

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test03(){

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
