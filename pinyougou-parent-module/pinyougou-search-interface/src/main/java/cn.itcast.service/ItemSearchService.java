package cn.itcast.service;

import java.util.Map;

/**
 * 搜索
 */
public interface ItemSearchService {

    Map<String,Object> search(Map searchMap);

}
