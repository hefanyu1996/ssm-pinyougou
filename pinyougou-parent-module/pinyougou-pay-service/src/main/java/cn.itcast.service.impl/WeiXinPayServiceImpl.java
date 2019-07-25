package cn.itcast.service.impl;


import cn.itcast.service.WeiXinPayService;
import cn.itcast.utils.HttpClient;
import cn.itcast.utils.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service(timeout = 5000)
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String mch_id;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;


    /**
     * 调用 微信支付系统 统一下单 API
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        System.out.println(out_trade_no + "::" + total_fee);

        //1.封装参数
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("out_trade_no", out_trade_no);
        paramMap.put("total_fee", total_fee);
        paramMap.put("appid", appid);
        paramMap.put("mch_id", mch_id);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", "品优购");
        paramMap.put("spbill_create_ip", "127.0.0.1");
        paramMap.put("notify_url", notifyurl);
        paramMap.put("trade_type", "NATIVE");


        try {
            String generateSignedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);//生成调用统一支付数据xml
            System.out.println("封装请求xml：" + generateSignedXml);
            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(generateSignedXml);
            httpClient.post();

            //3.获取结果
            String xmlResult = httpClient.getContent();//返回结果
            System.out.println("返回结果：" + xmlResult);

            //4.获取code_url
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);

            for (String s : mapResult.keySet()) {
                System.out.println(s + "::" + mapResult.get(s));
            }

            //5.设置页面展示数据
            HashMap<String, String> map = new HashMap<>();
            map.put("code_url", mapResult.get("code_url"));//二维码url
            map.put("total_fee", total_fee);//应付金额
            map.put("out_trade_no", out_trade_no);//订单号

            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<String, String>();

    }

    /**
     * 调用 微信支付系统 查询订单 API 订单状态
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {

        //1.封装参数
        HashMap<String, String> paramMap = new HashMap<>();

        paramMap.put("out_trade_no", out_trade_no);
        paramMap.put("appid", appid);
        paramMap.put("mch_id", mch_id);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, partnerkey));
            httpClient.post();
            //3.解析结果
            String payStatusRequest = httpClient.getContent();

            Map<String, String> mapResult = WXPayUtil.xmlToMap(payStatusRequest);

            return mapResult;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<String, String>();

    }


    /**
     * 关闭订单
     *
     * @param out_trade_no 秒杀订单号
     * @return
     */
    @Override
    public Map<String, String> closePay(String out_trade_no) {
        //1.封装参数
        HashMap<String, String> paramMap = new HashMap<>();

        paramMap.put("appid", appid);//公众账号ID
        paramMap.put("mch_id", mch_id);//商户号
        paramMap.put("out_trade_no", out_trade_no);//商户订单号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串

        try {//2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, partnerkey));
            httpClient.setHttps(true);
            httpClient.post();

            //3.解析结果
            String resultXml = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<String,String>();
    }


}
