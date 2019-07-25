package cn.itcast.service;

import java.util.List;

import cn.itcast.pojo.TbPayLog;
import cn.itcast.pojo.TbSeckillOrder;

import entity.PageResult;
import entity.Result;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SeckillOrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbSeckillOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbSeckillOrder seckillOrder);


    /**
     * 修改
     */
    public void update(TbSeckillOrder seckillOrder);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbSeckillOrder findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);


    /**
     * 提交订单
     *
     * @param seckillId 秒杀商品id
     * @param userId    用户id
     */
    public void submitOrder(Long seckillId, String userId);

    /**
     * 支付订单
     *
     * @param userId 当前登录用户id
     * @return
     */
    TbSeckillOrder searchPayLogFromRedis(String userId);


    /**
     * 支付成功保存订单
     *
     * @param userId
     * @param orderId
     * @param transactionId
     */
    void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);


    /**
     * 支付超时 删除缓存中订单 回退缓存中商品库存
     * @param userId
     * @param orderId
     */
    void deleteOrderFromRedis(String userId,Long orderId);

    /**
     * 查询用户是否已抢购（秒杀商品每种限购一次）
     * @param userId
     * @param seckillGoodsId
     * @return
     */
    Result findUserByOrderExist(String userId, Long seckillGoodsId);
}
