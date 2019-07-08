package cn.itcast.service.impl;

import cn.itcast.dao.TbGoodsDescMapper;
import cn.itcast.dao.TbGoodsMapper;
import cn.itcast.dao.TbItemCatMapper;
import cn.itcast.dao.TbItemMapper;
import cn.itcast.pojo.*;
import cn.itcast.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {

        Writer out = null;

        try {

            //获取configuration对象
            Configuration configuration = freeMarkerConfig.getConfiguration();

            //获取模板
            Template template = configuration.getTemplate("item.ftl");

            //创建数据模型
            Map dataModel = new HashMap();

            //根据商品id获取商品基本信息
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);

            dataModel.put("goods", tbGoods);

            //根据商品id获取商品扩展信息
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

            dataModel.put("goodsDesc", tbGoodsDesc);

            //获取三级商品类型面包屑
            TbItemCat tbItemCat1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            dataModel.put("itemCat1", tbItemCat1);
            dataModel.put("itemCat2", tbItemCat2);
            dataModel.put("itemCat3", tbItemCat3);

            //根据商品id获取商品sku列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//审核状态为1的商品
            criteria.andGoodsIdEqualTo(goodsId); //商品id
            example.setOrderByClause("is_default desc");//按默认sku排序
            List<TbItem> tbItemList = tbItemMapper.selectByExample(example);

            dataModel.put("skuList", tbItemList);

            //创建输出流
            out = new FileWriter(new File("e:\\item\\" + tbGoods.getId() + ".html"));

            //生成商品详情页
            template.process(dataModel, out);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 根据goodsId删除商品详情页
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeItemHtml(Long id) {

        try {

            new File(pageDir + id + ".html").delete();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return false;
    }
}
