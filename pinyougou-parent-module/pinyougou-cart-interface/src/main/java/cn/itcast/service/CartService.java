package cn.itcast.service;

import pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

}
