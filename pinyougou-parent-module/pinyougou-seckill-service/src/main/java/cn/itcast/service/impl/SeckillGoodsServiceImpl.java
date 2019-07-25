package cn.itcast.service.impl;

import cn.itcast.dao.TbSeckillGoodsMapper;
import cn.itcast.pojo.TbSeckillGoods;
import cn.itcast.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.itcast.pojo.TbSeckillGoodsExample;
import cn.itcast.pojo.TbSeckillGoodsExample.Criteria;
import entity.PageResult;
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
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.insert(seckillGoods);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillGoodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        Criteria criteria = example.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
            }
            if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
            }
            if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
            }
            if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
                criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
            }

        }

        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询正在参与秒杀的商品集合
     *
     * @return
     */
    @Override
    public List<TbSeckillGoods> findList() {

        /**
         * 从缓存中获取秒杀商品列表
         */
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();

        if (seckillGoodsList == null || seckillGoodsList.size() == 0 ) {//如果为空 则从数据库中查询 并存入redis

            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//商品状态为 1
            criteria.andStockCountGreaterThan(0);//商品库存大于0
            //商品秒杀时间范围
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            criteria.andEndTimeGreaterThanOrEqualTo(new Date());

            List<TbSeckillGoods> tbSeckillGoodsList = seckillGoodsMapper.selectByExample(example);

            for (TbSeckillGoods tbSeckillGoods : tbSeckillGoodsList) {

                redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(), tbSeckillGoods);

            }

            return tbSeckillGoodsList;

        } else {//否则直接返回redis中的列表

            return seckillGoodsList;

        }

    }

    /**
     * 根据id从缓存中查询秒杀商品
     */
    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {

        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);

    }


}
