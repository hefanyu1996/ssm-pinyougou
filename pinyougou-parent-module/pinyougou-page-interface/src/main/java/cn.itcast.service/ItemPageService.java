package cn.itcast.service;

/**
 * 商品详情页接口
 */
public interface ItemPageService {

    /**
     * 根据商品id生成商品详情页
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 根据商品id删除商品详情页
     * @param goodsId
     * @return
     */
    boolean removeItemHtml(Long goodsId);

}
