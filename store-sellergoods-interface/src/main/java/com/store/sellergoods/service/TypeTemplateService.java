package com.store.sellergoods.service;

import com.store.pojo.TbTypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface TypeTemplateService {

    /**
     * 返回全部列表
     *
     * @return
     */
    List<TbTypeTemplate> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    void add(TbTypeTemplate typeTemplate);


    /**
     * 修改
     */
    void update(TbTypeTemplate typeTemplate);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    TbTypeTemplate findOne(Long id);


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
    PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize);


    /**
     * 根据模板id查询规格列表
     * @param id
     * @return
     */
    List<Map> findSpecList(Long id);

}
