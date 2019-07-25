package cn.itcast.service;

import java.util.Map;

public interface WeiXinPayService {

    /**
     * 创建本地支付
     * @return
     */
    Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    Map<String, String> queryPayStatus(String out_trade_no);


    /**
     * 关闭订单
     * @param out_trade_no 秒杀订单号
     * @return
     */
    Map<String, String> closePay(String out_trade_no);
}
