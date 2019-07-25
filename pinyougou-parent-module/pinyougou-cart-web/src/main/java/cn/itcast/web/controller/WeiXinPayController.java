package cn.itcast.web.controller;


import cn.itcast.pojo.TbPayLog;
import cn.itcast.service.OrderService;
import cn.itcast.service.WeiXinPayService;
import cn.itcast.utils.IdWorker;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class WeiXinPayController {

    @Reference
    private WeiXinPayService weiXinPayService;

    @Reference
    private OrderService orderService;


    /**
     * 创建本地支付 获取二维码url
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String,String> createNative(){
        //获取当前用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户名查询支付日志
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);

        if (payLog != null) {
            return weiXinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
        }else{

            return new HashMap<>();
        }
        //提取支付日志中的订单号 和 金额 创建本地支付

    }


    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){

        Result result = null;

        //支付时限
        int time = 60;

        while (true) {
            //调用查询订单api
            Map<String, String> payStatus = weiXinPayService.queryPayStatus(out_trade_no);

            if (payStatus == null) {
                result = new Result(false, "支付发生异常");
                break;
            }

            if(payStatus.get("trade_state").equals("SUCCESS")){
                result = new Result(true, "支付成功");

                //支付成功 修改订单状态
                orderService.updateOrderStatus(out_trade_no,payStatus.get("transaction_id"));

                break;
            }

            try {
                Thread.sleep(3000);//定时查询订单
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("轮巡剩余时间："+time);

            time = time - 3;

            if(time <=0){
                result = new Result(false,"二维码超时");
                break;
            }
        }

            return result;
    }

}
