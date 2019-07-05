package cn.itcast.service.impl;

import cn.itcast.dao.TbItemCatMapper;
import cn.itcast.pojo.TbItem;
import cn.itcast.pojo.TbItemCat;
import cn.itcast.pojo.TbItemCatExample;
import cn.itcast.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbItemCat itemCat) {
        itemCatMapper.insert(itemCat);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbItemCat itemCat) {
        itemCatMapper.updateByPrimaryKey(itemCat);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbItemCat findOne(Long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) throws Exception {

        for (Long id : ids) {
            TbItemCatExample itemCatExample = new TbItemCatExample();
            TbItemCatExample.Criteria criteria = itemCatExample.createCriteria();
            criteria.andParentIdEqualTo(id);
            List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(itemCatExample);

            if (tbItemCats!=null && tbItemCats.size()>0) {
                throw new Exception();
            }
        }

        for (Long id : ids) {
            itemCatMapper.deleteByPrimaryKey(id);
        }


    }


    @Override
    public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (itemCat.getName() != null && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }

        }

        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    /**
     * 根据parentId
     *
     * @param parentId
     * @return
     */
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {

        TbItemCatExample itemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = itemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);



        //每次刷新时缓存 商品分类名称 和 对应的模板id
        //每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)

        List<TbItemCat> itemCatList = findAll();
        for (TbItemCat tbItemCat : itemCatList) {
            String name = tbItemCat.getName();
            Long typeId = tbItemCat.getTypeId();

            redisTemplate.boundHashOps("itemCat").put(name,typeId);
        }

        System.out.println("更新缓存：商品分类表");

        return itemCatMapper.selectByExample(itemCatExample);
    }



}
