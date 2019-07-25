package cn.itcast.task;

import cn.itcast.dao.TbSeckillGoodsMapper;
import cn.itcast.pojo.TbSeckillGoods;
import cn.itcast.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {
    /**
     * 刷新秒杀商品
     */

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron="0 * * * * ?")
    public void refreshSeckillGoods(){

        System.out.println("Task 定时执行更新缓存秒杀商品:"+new Date());

        //查询缓存中秒杀商品ID集合
        List<Long> seckillGoodsIdList = new ArrayList<Long>(redisTemplate.boundHashOps("seckillGoods").keys());

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//商品状态为 1
        criteria.andStockCountGreaterThan(0);//商品库存大于0
        //商品秒杀时间范围
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());

        if(seckillGoodsIdList.size()>0){
            criteria.andIdNotIn(seckillGoodsIdList); //排除缓存中已存在的商品
        }

        List<TbSeckillGoods> tbSeckillGoodsList = seckillGoodsMapper.selectByExample(example);

        for (TbSeckillGoods tbSeckillGoods : tbSeckillGoodsList) {

            redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);
            System.out.println("增量更新商品："+tbSeckillGoods.getTitle());
        }
        System.out.println(".....end......");
    }


    /**
     * 移除过期秒杀商品
     */
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        //从缓存中获取商品集合
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        for (TbSeckillGoods seckill : seckillGoodsList) {

            if(seckill.getEndTime().getTime() <= new Date().getTime()){//如果商品已过期

                seckillGoodsMapper.updateByPrimaryKey(seckill);//向数据库保存记录

                redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());//移除缓存

                System.out.println(seckill.getTitle()+"-商品已过期-商品id:"+seckill.getId());

            }

        }
        System.out.println("执行了清除秒杀商品的任务："+new Date());

    }

}
