package com.store.content.service;

import com.store.pojo.TbContentCategory;
import entity.PageResult;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ContentCategoryService {

	/**
	 * 返回全部列表
	 * @return
	 */
	 List<TbContentCategory> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	 void add(TbContentCategory contentCategory);
	
	
	/**
	 * 修改
	 */
	 void update(TbContentCategory contentCategory);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	 TbContentCategory findOne(Long id);
	
	
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
	 PageResult findPage(TbContentCategory contentCategory, int pageNum, int pageSize);
	
}
