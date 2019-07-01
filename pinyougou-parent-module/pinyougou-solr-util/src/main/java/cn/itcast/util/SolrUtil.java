package cn.itcast.util;


import cn.itcast.dao.TbItemMapper;
import cn.itcast.pojo.TbItem;
import cn.itcast.pojo.TbItemExample;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 查询商品SKU数据
     */
    public void importItemData(){
        //根据条件查询
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        //设置条件 SKU状态为1(启用)
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);

        //将规格数据导入索引库
        for (TbItem tbItem : tbItemList) {
            System.out.println(tbItem.getTitle()+"-"+tbItem.getPrice());
            //获取规格json字符串
            String spec = tbItem.getSpec();
            //将规格字符串转Map
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            //设置实体类动态域映射的属性
            tbItem.setSpecMap(specMap);
        }
        //导入商品数据
        solrTemplate.saveBeans(tbItemList);
        solrTemplate.commit();
    }

    public static void main(String[] args) {

        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");

        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");

        solrUtil.deleteAll();

//        solrUtil.importItemData();


    }


    /**
     * 删除所有数据
     */
    public void deleteAll(){

        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }





}
