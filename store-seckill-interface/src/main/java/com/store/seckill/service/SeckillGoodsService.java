package com.store.seckill.service;
import com.store.pojo.TbSeckillGoods;
import entity.PageResult;

import java.util.List;
/**
 * 秒杀商品服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	 List<TbSeckillGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	 void add(TbSeckillGoods seckillGoods);
	
	
	/**
	 * 修改
	 */
	 void update(TbSeckillGoods seckillGoods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	 TbSeckillGoods findOne(Long id);
	
	
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
	 PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize);
	
	/**
	 * 返回正在参与秒杀的商品
	 * @return
	 */
	 List<TbSeckillGoods> findList();
	
	
	/**
	 * 根据ID获取实体(从缓存中读取)
	 * @param id
	 * @return
	 */
	 TbSeckillGoods findOneFromRedis(Long id);
	
}
