package cn.itcast.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String, String> map) {

        try {
            SendSmsResponse sendSmsResponse = smsUtils.sendSms(
                    map.get("mobile"),          //手机号
                    map.get("template_code"),   //短信模板
                    map.get("sign_name"),       //标签
                    map.get("param"));//表达式参数

            System.out.println("code:"+sendSmsResponse.getCode());
            System.out.println("message:"+sendSmsResponse.getMessage());
            System.out.println("requestId:"+sendSmsResponse.getRequestId());
            System.out.println("bizId:"+sendSmsResponse.getBizId());
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

}
