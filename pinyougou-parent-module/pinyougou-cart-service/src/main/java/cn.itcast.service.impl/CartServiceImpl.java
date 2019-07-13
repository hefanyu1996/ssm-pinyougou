package cn.itcast.service.impl;

import cn.itcast.dao.TbItemMapper;
import cn.itcast.pojo.TbItem;
import cn.itcast.pojo.TbOrderItem;
import cn.itcast.pojo.TbOrderItemExample;
import cn.itcast.service.CartService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import pojogroup.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(timeout = 100000)
public class CartServiceImpl implements CartService {


    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 添加商品到购物车
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据skuid  获取 商家id
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

        if (tbItem == null) {
            throw new RuntimeException("商品不存在");
        }

        if (!tbItem.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }

        //2.根据商家id 判断购物车列表是否存在该商家购物车对象
        String sellerId = tbItem.getSellerId();

        Cart cart = searchCartBySellarId(cartList, sellerId);//判断商家购物车对象是否存在

        if (cart == null) {//3 商家购物车不存在
            // 3.1  创建一个新的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());

            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem tbOrderItem = createOrderItem(tbItem, num);
            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);

            //3.2 将新建购物车对象添加购物车列表
            cartList.add(cart);

        } else {//4 商家购物车存在
            //根据sku id 判断商品明细 是否存在
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = searchOrderItemByItemId(orderItemList, itemId);

            if (orderItem == null) {
                //4.1 不存在
                //创建新的商品明细对象  设置数量 和金额
                orderItem = createOrderItem(tbItem, num);

                //将新商品明细对象设置到商品明细列表
                orderItemList.add(orderItem);

            } else {
                //4.2 存在
                //将原有商品明细  数量、金额增加
                orderItem.setNum(orderItem.getNum() + num);

                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                //如果数量操作后小于等于0 则移除
                if(orderItem.getNum() <= 0){
                    orderItemList.remove(orderItem);
                }

                //如果移除后orderItemList长度小于等于0 则移除购物车对象
                if(orderItemList.size() <= 0){
                    cartList.remove(cart);
                }

            }


        }

        return cartList;
    }

    /**
     * 根据skuid查询商品明细
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {

        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }

        return null;
    }

    /**
     * 创建订单明细
     *
     * @param tbItem
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {

        if (num < 0) {
            throw new RuntimeException("数量非法");
        }

        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setNum(num);
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue() * num));
        tbOrderItem.setSellerId(tbItem.getSellerId());

        return tbOrderItem;
    }

    /**
     * 根据商家id 查询购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellarId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
