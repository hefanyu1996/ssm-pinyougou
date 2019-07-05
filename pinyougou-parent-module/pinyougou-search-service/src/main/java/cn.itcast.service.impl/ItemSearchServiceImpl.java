package cn.itcast.service.impl;

import cn.itcast.pojo.TbItem;
import cn.itcast.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        Map<String, Object> resultMap = new HashMap<>();

        //关键字空格处理
        String keywords = (String) searchMap.get("keywords");

        searchMap.put("keywords", keywords.replace(" ",""));//去除空格

        //1.按关键字查询（高亮显示）
        resultMap.putAll(searchHighlightList(searchMap));

        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        resultMap.put("categoryList", categoryList);


        //3.根据分类查询品牌和规格集合
        String category = (String) searchMap.get("category");
        if (!"".equals(category)) {
            resultMap.putAll(searchBrandAndSpecList(category));
        } else {
            if (categoryList != null && categoryList.size() > 0) {
                resultMap.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }


        return resultMap;
    }


    /**
     * 根据关键字分组查询商品分类
     *
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap) {
        //创建主查询条件 默认 *:*
        Query query = new SimpleQuery();

        //1.0设置分组查询过滤条件
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //1.1设置分组的域
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //2.执行分组查询 得到分组页结果
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);

        //3.解析分组查询结果
        //3.1 根据列得到分组结果
        GroupResult<TbItem> itemCategory = groupPage.getGroupResult("item_category");
        //3.2 得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = itemCategory.getGroupEntries();
        //3.3 得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        //3.4 创建返回集合
        List<String> list = new ArrayList<String>();

        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }

        return list;
    }


    /**
     * 搜索关键字结果高亮查询
     *
     * @param searchMap
     * @return
     */
    private Map<String, Object> searchHighlightList(Map searchMap) {
        HighlightQuery query = new SimpleHighlightQuery(); // 设置高亮查询主条件 默认*:*
        HighlightOptions highlightOptions = new HighlightOptions();
        //构建高亮选项对象
        highlightOptions.addField("item_title");//设置高亮域
        highlightOptions.setSimplePrefix("<span style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</span>");//后缀

        query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项

        //1.1关键字查询
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2 按商品分类过滤
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3 按品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4 按规格过滤

        if (searchMap.get("spec") != null) {

            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }

        //1.5 按价格过滤

        if (!"".equals(searchMap.get("price"))) {

            String[] price = ((String) searchMap.get("price")).split("-");

            if (!"0".equals(price[0])) {//如果区间起点不等于0

                Criteria filterCriteria = new Criteria("item_price");

                filterCriteria.greaterThanEqual(price[0]);

                FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);

                query.addFilterQuery(filterQuery);

            }


            if (!"*".equals(price[1])) {//如果区间终点不等于*

                Criteria filterCriteria = new Criteria("item_price");

                filterCriteria.lessThanEqual(price[1]);

                FilterQuery filterQuery = new SimpleFacetQuery(filterCriteria);

                query.addFilterQuery(filterQuery);

            }

        }

        // 1.6 分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if(pageNo==null){
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

        //1.7 价格排序

        Sort priceSort = null;
        if("ASC".equals(searchMap.get("sort"))){
            priceSort = new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField"));
        }else if("DESC".equals(searchMap.get("sort"))){
            priceSort = new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField"));
        }

        query.addSort(priceSort);


        //******************* 获取高亮结果集 *******************
        //高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //高亮入口集合
        for (HighlightEntry<TbItem> tbItemHighlightEntry : page.getHighlighted()) {
            List<HighlightEntry.Highlight> highlightList = tbItemHighlightEntry.getHighlights();
            //获取高亮列表
            /*for (HighlightEntry.Highlight highlight : tbItemHighlightEntry.getHighlights()) {
                for (String snipplet : highlight.getSnipplets()) {
                    tbItemHighlightEntry.getEntity().setTitle(snipplet);
                }
            }*/
            if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0) {
                String snipplet = tbItemHighlightEntry.getHighlights().get(0).getSnipplets().get(0);
                tbItemHighlightEntry.getEntity().setTitle(snipplet);
            }


        }

        //创建map返回
        Map<String, Object> map = new HashMap<>();
        map.put("rows", page.getContent());//设置高亮页数据集合
        map.put("total",page.getTotalElements());//设置总记数
        map.put("totalPages",page.getTotalPages());//设置总页数
        return map;

    }


    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据商品分类查询 品牌和规格集合
     *
     * @return
     */
    private Map<String, Object> searchBrandAndSpecList(String category) {

        Map<String, Object> map = new HashMap<>();

        Object typeId = redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null) {

            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);

            map.put("brandList", brandList);

            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);

            map.put("specList", specList);
        }

        return map;
    }


}
