package cn.itcast.web.controller;

import cn.itcast.service.CartService;
import cn.itcast.utils.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.Cart;

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


    @RequestMapping("/getLoginUser")
    public String getLoginUser(){
       return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){

        //获取当前登录用户名
        String loginUser = getLoginUser();

        String cartListString = CookieUtil.getCookieValue(request,"cartList","utf-8");

        if("".equals(cartListString) || cartListString == null ){

            cartListString = "[]";

        }
        //将json 转为集合
        List<Cart> cookies = JSON.parseArray(cartListString, Cart.class);

        if("anonymousUser".equals(loginUser)){//用户未登陆

            return cookies;

        }else{//用户已登录

            List<Cart> redis = cartService.findCartListFormRedis(loginUser);
            if(cookies.size() > 0){
                //如果cookie中购物车列表不为空
                redis = cartService.mergeCartList(redis, cookies);

                //将合并后的购物车存入redis（远程购物车）
                cartService.saveCartListToRedis(loginUser,redis);

                //清除cookie（本地购物车）中的购物车列表
                CookieUtil.deleteCookie(request,response,"cartList");

            }

            return redis;

        }

    }


    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9876")//注解式cors跨域解决方案
    public Result addGoodsToCartList(Long itemId, Integer num){
        //cors跨域解决方案
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9876");
//        response.setHeader("Access-Control-Allow-Credentials", "true");

        //获取当前登录用户id
        String loginUser = getLoginUser();

        try {
            //获取当前购物车列表
            List<Cart> cartList = findCartList();
            //修改商品后返回新的购物车列表
            cartList= cartService.addGoodsToCartList(cartList, itemId, num);

            if("anonymousUser".equals(loginUser)){//用户未登录
                //设置新的列表至cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");

            }else{//用户已登陆

                //登录后将对应用户的购物车列表存入redis
                cartService.saveCartListToRedis(loginUser,cartList);

            }

            return new Result(true,"商品添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,"商品添加失败");

    }





}
