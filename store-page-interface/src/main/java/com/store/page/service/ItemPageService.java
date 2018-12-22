package com.store.page.service;

public interface ItemPageService {


    /**
     * 生产商品详情页
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详情页
     * @param goodsIds
     * @return
     */
    boolean deleteItemHtml(Long[] goodsIds);
}
