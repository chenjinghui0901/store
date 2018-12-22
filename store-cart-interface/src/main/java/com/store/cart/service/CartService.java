package com.store.cart.service;

import com.store.pojo.group.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 从redis中提取购物车列表
     * @param userName
     * @return
     */
    List<Cart> findCartListFromRedis(String userName);


    /**
     * 将购物车列表存入redis中
     * @param cartList
     * @param userName
     */
    void saveCartListToRedis(List<Cart> cartList, String userName);

    /**
     * 合并cookie和redis购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
