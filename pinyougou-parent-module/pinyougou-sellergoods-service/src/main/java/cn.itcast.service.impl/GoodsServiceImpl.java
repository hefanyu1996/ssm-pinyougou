package cn.itcast.service.impl;

import cn.itcast.dao.*;
import cn.itcast.pojo.*;
import cn.itcast.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.Goods;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //插入商品基本信息
        TbGoods tbGoods = goods.getTbGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.insert(tbGoods);


        //获取插入的基本信息商品的id
        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        //设置为商品扩展信息主键  1：1
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        //插入商品扩展信息
        goodsDescMapper.insert(tbGoodsDesc);

        //插入商品SKU列表
        insertItemList(goods);

    }


    private void insertItemList(Goods goods) {
        if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
            //插入ItemList信息
            List<TbItem> tbItemList = goods.getTbItemList();
            for (TbItem tbItem : tbItemList) {
                //构建标题 SPU名称+ 规格选项值
                String title = goods.getTbGoods().getGoodsName();//SPU名称

                Map<String, Object> specMap = JSON.parseObject(tbItem.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                //设置SPU名称
                tbItem.setTitle(title);
                //设置商品id
                tbItem.setGoodsId(goods.getTbGoods().getId());
                //设置末级分类
                tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
                //设置商家id
                tbItem.setSellerId(goods.getTbGoods().getSellerId());
                //设置上架日期

                setItemValues(goods, tbItem);

                itemMapper.insert(tbItem);
            }
        } else {

            TbItem tbItem = new TbItem();
            //构建标题 SPU名称+ 规格选项值
            String title = goods.getTbGoods().getGoodsName();//SPU名称
            //设置SPU名称
            tbItem.setTitle(title);
            //设置价格
            tbItem.setPrice(goods.getTbGoods().getPrice());
            //设置库存
            tbItem.setNum(99999);
            //设置状态
            tbItem.setStatus("0");
            //设置是否默认
            tbItem.setIsDefault("1");
            //设置spec
            tbItem.setSpec("{}");

            setItemValues(goods, tbItem);

            itemMapper.insert(tbItem);

        }
    }

    private void setItemValues(Goods goods, TbItem tbItem) {
        //设置商品id
        tbItem.setGoodsId(goods.getTbGoods().getId());
        //设置末级分类
        tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
        //设置商家id
        tbItem.setSellerId(goods.getTbGoods().getSellerId());
        //设置上架日期
        tbItem.setCreateTime(new Date());
        //设置修改日期
        tbItem.setUpdateTime(new Date());
        //设置品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
        //分类名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
        tbItem.setCategory(tbItemCat.getName());
        //商家店铺名称
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
        tbItem.setSeller(tbSeller.getNickName());
        //图片地址（取spu的第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
        if (imageList.size() > 0) {
            tbItem.setImage((String) imageList.get(0).get("url"));
        }

    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //修改商品基本信息
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        //修改商品扩展信息
        goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        //修改商品SKU
        //1.删除该商品的SKU
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
        itemMapper.deleteByExample(tbItemExample);
        //2.插入新的SKU
        insertItemList(goods);

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {

        Goods goods = new Goods();
        //查询并设置商品基本信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);

        //查询并设置商品扩展信息
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setTbGoodsDesc(tbGoodsDesc);

        //根据商品id查询并
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(tbItemExample);
        //设置商品SKU列表
        goods.setTbItemList(tbItems);

        return goods;
    }

    /**
     * 批量删除（逻辑删除）
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //逻辑删除
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");

            goodsMapper.updateByPrimaryKey(tbGoods);
//            goodsMapper.deleteByPrimaryKey(id);

        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();

        //只显示isDelete为null的商品（逻辑删除）
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 商品审核
     * @param ids 商品id数组
     * @param auditStatus 更改状态码
     */
    @Override
    public void auditGoods(Long[] ids, String auditStatus) {

        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            //设置商品审核状态
            tbGoods.setAuditStatus(auditStatus);

            //修改指定id的商品审核状态
            TbGoodsExample example = new TbGoodsExample();
            TbGoodsExample.Criteria criteria = example.createCriteria();
            //设置商品id

            goodsMapper.updateByPrimaryKey(tbGoods);
        }

    }


    /**
     * 商品上/下架
     * @param ids
     * @param marketable
     */
    @Override
    public void setMarketable(Long[] ids, String marketable) {
        for( Long id : ids ){
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsMarketable(marketable);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }

    }

    /**
     * 根据goodsIds 和 status 查询suk列表
     * @param goodsIds
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        //根据goodsIds查询的sku列表
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        //设置状态条件
        criteria.andStatusEqualTo(status);
        List<TbItem> tbItemList = itemMapper.selectByExample(example);

        return tbItemList;
    }


}
