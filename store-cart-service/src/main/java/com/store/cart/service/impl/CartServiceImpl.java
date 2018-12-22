package com.store.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.store.cart.service.CartService;
import com.store.mapper.TbItemMapper;
import com.store.pojo.TbItem;
import com.store.pojo.TbOrderItem;
import com.store.pojo.group.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        // 1.根据skuId查询商品明细SKU的对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品状态不合法");
        }
        // 2.根据sku对象得到商家id
        String sellerId = item.getSellerId();

        // 3.根据商家id在购物车列表中查询购物车对象
        Cart cart = searchCartBySellerId(cartList, sellerId);

        // 4.如果购物车列表中不存在该商家购物车
        if (cart == null) {
            // 4.1 创建一个新的购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList<>();
            cart.setOrderItemList(orderItemList);
            // 创建新的购物车明细列表
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);

            cart.setOrderItemList(orderItemList);
            // 4.2将新的购物车对象添加到购物车列表中
            cartList.add(cart);
        } else {
            // 5.如果购物车列表中存在该商品的购物车
            // 5.1判断该商品是否在该购物车的列表中存在
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), item.getId());

            // 5.2如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中
            if (orderItem == null) {
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                // 5.3 如果存在，在原有的数量上添加。并且更新金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                // 当明细数量<=0,则移除此明细
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                // 当购物车列表的明细数量为0，则移除该购物车
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        System.out.println("从redis中提取购物车");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(List<Cart> cartList, String userName) {
        System.out.println("向redis存入购物车");
        redisTemplate.boundHashOps("cartList").put(userName, cartList);
    }

    /**
     * 根据商家Id在购物车列表中查询购物车对象
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 根据skuid在购物车命令列表中查询购物车明细对象
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().equals(itemId)) {
                return orderItem;
            }
        }
        return null;
    }


    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        // cartList1.addAll(cartList2);  不能简单合并
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(),
                        orderItem.getNum());
            }
        }
        return cartList1;
    }

}
