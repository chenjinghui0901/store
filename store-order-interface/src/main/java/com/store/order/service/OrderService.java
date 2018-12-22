package com.store.order.service;
import com.store.pojo.TbOrder;
import com.store.pojo.TbPayLog;
import entity.PageResult;

import java.util.List;
/**
 * 服务层接口
 * @author Administrator
 *
 */
 public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	 List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	 void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	 void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	 TbOrder findOne(Long id);
	
	
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
	 PageResult findPage(TbOrder order, int pageNum, int pageSize);
	
	/**
	 * 根据用户ID获取支付日志
	 * @param userId
	 * @return
	 */
	 TbPayLog searchPayLogFromRedis(String userId);
	
	
	/**
	 * 支付成功修改状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	 void updateOrderStatus(String out_trade_no, String transaction_id);
	
	
}
