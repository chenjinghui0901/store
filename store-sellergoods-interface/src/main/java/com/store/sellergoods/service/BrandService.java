package com.store.sellergoods.service;

import com.store.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 */
public interface BrandService {

    List<TbBrand> findAll();

    /**
     * 品牌分页
     * @param pageNum 当前页面
     * @param pageSize 每页记录数
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);

    /**
     * 增加
     * @param brand
     */
    void add(TbBrand brand);

    TbBrand findOne(Long id);

    void update(TbBrand brand);

    void delete(Long[] ids);

    PageResult findPage(TbBrand brand, int pageNum, int pageSize);

	List<Map> selectOptionList();
}
