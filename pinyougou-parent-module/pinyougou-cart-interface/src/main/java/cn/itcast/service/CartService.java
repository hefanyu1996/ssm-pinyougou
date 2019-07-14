package cn.itcast.service;

import pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);
    /**
     * 从redis中查询cartList
     */
    List<Cart> findCartListFormRedis(String username);

    /**
     * 将购物车列表保存到redis
     */
    void saveCartListToRedis(String username,List<Cart> cartList);


    /**
     * 合并cookie和redis购物车
     */
    List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
