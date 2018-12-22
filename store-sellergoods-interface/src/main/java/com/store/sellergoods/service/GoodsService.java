package com.store.sellergoods.service;

import com.store.pojo.TbGoods;
import com.store.pojo.TbItem;
import com.store.pojo.group.Goods;
import entity.PageResult;

import java.util.List;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface GoodsService {

    /**
     * 返回全部列表
     *
     * @return
     */
    List<TbGoods> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    void add(Goods goods);


    /**
     * 修改
     */
    void update(Goods goods);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    Goods findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    PageResult findPage(TbGoods goods, int pageNum, int pageSize);

    /**
     * 批量修改状态
     *
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 根据spu的id集合查询sku列表（spu 1 : sku n)
     * @param goodsid
     * @param status
     * @return
     */
    List<TbItem> findItemListByGoodsIdAndStatus(Long[] goodsid, String status);
}
