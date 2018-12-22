package com.store.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.store.cart.service.CartService;
import com.store.pojo.group.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

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

    /**
     * 提取购物车
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        //当前登录人账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人：" + username);

        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (StringUtils.isBlank(cartListStr)) {
            cartListStr = "[]";
        }
        List<Cart> cartListCookie = JSON.parseArray(cartListStr, Cart.class);
        if ("anonymousUser".equals(username)) {//如果未登录
            //从cookie中提取购物车
            System.out.println("从cookie中提取购物车");

            return cartListCookie;

        } else {//如果已登录
            //获取redis购物车
            List<Cart> cartListRedis = cartService.findCartListFromRedis(username);
            if (cartListCookie.size() > 0) {//判断当本地购物车中存在数据
                //得到合并后的购物车
                List<Cart> cartList = cartService.mergeCartList(cartListCookie, cartListRedis);
                //将合并后的购物车存入redis
                cartService.saveCartListToRedis(cartList, username);
                //本地购物车清除
                CookieUtil.deleteCookie(request, response, "cartList");
                System.out.println("执行了合并购物车的逻辑");
                return cartList;
            }
            return cartListRedis;
        }
    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(Long itemId, Integer num) {

//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Credentials", true);

        //当前登录人账号
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人：" + name);

        try {
            //提取购物车
            List<Cart> cartList = findCartList();
            //调用服务方法操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if ("anonymousUser".equals(name)) {//如果未登录
                //将新的购物车存入cookie
                String cartListString = JSON.toJSONString(cartList);
                util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");
                System.out.println("向cookie存储购物车");

            } else {//如果登录
                cartService.saveCartListToRedis(cartList, name);
            }

            return new Result(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }
    }


}
