package cn.itcast.service;

import cn.itcast.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * 搜索
 */
public interface ItemSearchService {

    Map<String,Object> search(Map searchMap);

    void importList(List<TbItem> list);

    void deleteSolrSku(List ids);

}
