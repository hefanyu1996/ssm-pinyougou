package cn.itcast.service.impl;

import cn.itcast.dao.TbOrderItemMapper;
import cn.itcast.dao.TbOrderMapper;
import cn.itcast.dao.TbPayLogMapper;
import cn.itcast.pojo.*;
import cn.itcast.pojo.TbOrderExample.Criteria;
import cn.itcast.service.OrderService;
import cn.itcast.utils.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //创建集合 存入所有订单id
        List<String> orderIdList = new ArrayList<>();
        //累加所有订单金额
        double total_money = 0;

        for (Cart cart : cartList) {

            TbOrder tbOrder = new TbOrder();
            long orderId = idWorker.nextId();
            tbOrder.setOrderId(orderId);
            tbOrder.setPaymentType(order.getPaymentType());
            tbOrder.setStatus("1");//未付款
            tbOrder.setCreateTime(new Date());//创建时间
            tbOrder.setUpdateTime(new Date());//更新时间
            tbOrder.setUserId(order.getUserId());//用户id
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货地址
            tbOrder.setReceiver(order.getReceiver());//收货人
            tbOrder.setReceiverMobile(order.getReceiverMobile());//收货人联系方式
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家id

            //循环购物车明细
            double totalMoney = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());//订单明细id

                orderItem.setOrderId(orderId);//订单id
                orderItem.setSellerId(cart.getSellerId());
                totalMoney += orderItem.getTotalFee().doubleValue();

                orderItemMapper.insert(orderItem);//插入订单明细

            }
            tbOrder.setPayment(new BigDecimal(totalMoney));//订单总金额
            orderMapper.insert(tbOrder);

            orderIdList.add(orderId + "");
            total_money += totalMoney;

        }
        /* 支付日志
	            （1）在用户下订单时，判断如果为微信支付，就向支付日志表添加一条记录，
                信息包括支付总金额、订单ID（多个）、用户ID 、下单时间等信息，支付状态
                为0（未支付）
	         */

        if ("1".equals(order.getPaymentType())) {

            TbPayLog payLog = new TbPayLog();

            payLog.setOutTradeNo(idWorker.nextId() + "");//支付订单号
            payLog.setUserId(order.getUserId());//用户id
            payLog.setOrderList(orderIdList.toString().replace("[", "").replace("]", "").replace(" ", ""));//订单id集合
            payLog.setTotalFee((long) (total_money * 100));//总金额
            payLog.setTradeState("0");//交易状态
            payLog.setPayType(order.getPaymentType());//支付类型 ： 微信

            payLog.setCreateTime(new Date());//日志创建时间
            payLogMapper.insert(payLog);    //将支付日志插入数据库

            redisTemplate.boundHashOps("payLog").put(payLog.getUserId(), payLog);//将支付日志存入redis
        }
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());

    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据用户名从缓存中查询支付日志
     *
     * @param userId
     * @return
     */
    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {

        TbPayLog payLog = (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);

        return payLog;
    }

    /**
     * 支付成功后 修改订单状态
     *
     * @param out_trade_no
     * @param transaction_id
     * @return
     */
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {

        //1.修改payLog中订单状态
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());//支付时间
        payLog.setTradeState("1");// 交易状态为1  表示交易成功
        payLog.setTransactionId(transaction_id);//交易流水号
        payLogMapper.updateByPrimaryKey(payLog);

        //2.修改order订单状态
        String[] orderIds = payLog.getOrderList().split(",");
        for (String orderId : orderIds) {

            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));

            if (tbOrder != null) {
                tbOrder.setStatus("2"); //交易状态为2  表示订单已完成支付
                orderMapper.updateByPrimaryKey(tbOrder);
            }

        }

        //3.清除缓存中的订单信息
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());

    }

}
