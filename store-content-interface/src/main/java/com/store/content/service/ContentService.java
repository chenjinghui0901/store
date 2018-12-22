package com.store.content.service;

import com.store.pojo.TbContent;
import entity.PageResult;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ContentService {

	/**
	 * 返回全部列表
	 * @return
	 */
	 List<TbContent> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	 void add(TbContent content);
	
	
	/**
	 * 修改
	 */
	 void update(TbContent content);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	 TbContent findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	 void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	 PageResult findPage(TbContent content, int pageNum, int pageSize);
	
	/**
	 * 根据广告分类ID查询广告列表
	 * @param categoryId
	 * @return
	 */
	 List<TbContent> findByCategoryId(Long categoryId);
	
}
