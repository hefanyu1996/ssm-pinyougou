package cn.itcast.service.impl;

import cn.itcast.dao.TbSeckillGoodsMapper;
import cn.itcast.dao.TbSeckillOrderMapper;
import cn.itcast.pojo.TbPayLog;
import cn.itcast.pojo.TbSeckillGoods;
import cn.itcast.pojo.TbSeckillOrder;
import cn.itcast.pojo.TbSeckillOrderExample;
import cn.itcast.service.SeckillOrderService;
import cn.itcast.utils.IdWorker;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        TbSeckillOrderExample.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /**
     * 提交订单
     *
     * @param seckillId 秒杀商品id
     * @param userId    用户id
     */
    @Override
    public void submitOrder(Long seckillId, String userId) {


        System.out.println(seckillId);
        //从缓存中
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);

        if (seckillGoods == null) {

            throw new RuntimeException("商品不存在");

        }

        if (seckillGoods.getStockCount() <= 0) {

            throw new RuntimeException("商品已被抢光");

        }

        //库存-1
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //同步redis
        redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);

        if (seckillGoods.getStockCount() == 0) {//如果商品被抢光

            //将卖光的商品同步数据库
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            //删除redis中库存为0商品
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);

        }

        //封装商品订单 存入redis
        TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
        tbSeckillOrder.setId(idWorker.nextId());//订单id
        tbSeckillOrder.setSeckillId(seckillId);//商品id
        tbSeckillOrder.setUserId(userId);//下单用户id
        tbSeckillOrder.setSellerId(seckillGoods.getSellerId());//商家id
        tbSeckillOrder.setCreateTime(new Date());//订单创建时间
        tbSeckillOrder.setStatus("0");//订单状态
        tbSeckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格

        //将订单存入redis
        redisTemplate.boundHashOps("seckillOrder").put(userId, tbSeckillOrder);

    }


    /**
     * 从redis根据用户id获取订单
     *
     * @param userId 当前登录用户id
     * @return
     */
    @Override
    public TbSeckillOrder searchPayLogFromRedis(String userId) {

        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

    }

    /**
     * 支付成功保存订单从redis到mysql
     *
     * @param userId
     * @param orderId
     * @param transactionId
     */
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {

        System.out.println("saveOrderFromRedisToDb:" + userId);
        //根据用户ID查询订单

        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        if (seckillOrder == null) {

            throw new RuntimeException("订单不存在");

        }

        if (seckillOrder.getId().longValue() != orderId.longValue()) {

            throw new RuntimeException("订单不匹配");

        }

        seckillOrder.setStatus("1");//支付状态
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setTransactionId(transactionId);//流水号

        //将订单存入数据库
        seckillOrderMapper.insert(seckillOrder);

        //清除redis中订单数据
        redisTemplate.boundHashOps("seckillOrder").delete(userId);

    }

    /**
     * 支付超时 删除缓存中订单  回退秒杀商品库存
     *
     * @param userId
     * @param orderId
     */
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {

        //根据userId从redis中查询超时订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        //存在
        if (seckillOrder != null && seckillOrder.getId().longValue() == orderId.longValue()) {

            //删除超时订单
            redisTemplate.boundHashOps("seckillOrder").delete(userId);


            //恢复库存
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());

            if (seckillGoods != null) {//秒杀商品存在

                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);//库存回退

                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);//存入redis

            } else {

                //从数据库中查出库存正好清空的商品
                TbSeckillGoods tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
                //库存回退为1
                tbSeckillGoods.setStockCount(1);
                //存回redis
                redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), tbSeckillGoods);

            }

        }
    }

    /**
     * 查询该商品是否已经下单（秒杀商品一个用户只能购买一次）
     * @param userId
     * @param seckillGoodsId
     */
    @Override
    public Result findUserByOrderExist(String userId, Long seckillGoodsId) {

        //获取订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        if(seckillOrder !=null){

            if(seckillOrder.getSeckillId().longValue() == seckillGoodsId){

                return  new Result(false,"failed");

            }

        }

        return new Result(true,"success");
    }

}
