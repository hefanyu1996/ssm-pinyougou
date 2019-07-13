package cn.itcast.web.controller;

import cn.itcast.service.CartService;
import cn.itcast.utils.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.Cart;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String cartListString = CookieUtil.getCookieValue(request,"cartList","utf-8");

        if("".equals(cartListString) ||cartListString == null ){

            cartListString = "[]";
        }

        //将json 转为集合
        List<Cart> cartList = JSON.parseArray(cartListString, Cart.class);

        return cartList;

    }


    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){

        try {
            //获取当前购物车列表
            List<Cart> cartList = findCartList();
            //修改商品后返回新的购物车列表
            cartList= cartService.addGoodsToCartList(cartList, itemId, num);

            //设置新的列表至cookie
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
            return new Result(true,"商品添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,"商品添加失败");

    }



}
