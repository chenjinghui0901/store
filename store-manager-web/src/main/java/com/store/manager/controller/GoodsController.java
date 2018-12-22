package com.store.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.store.pojo.TbGoods;
import com.store.pojo.TbItem;
import com.store.pojo.group.Goods;
import com.store.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private JmsTemplate jmsTemplate;

	// 点对点
	@Autowired
	private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDeleteDestination;

	// 发布订阅
	@Autowired
	private Destination topicPageDestination;

	@Autowired
	private Destination topicPageDeleteDestination;

//	去耦合
//	@Reference
//	private ItemPageService itemPageService;

//	@Reference(timeout = 10000)
//	private ItemSearchService itemSearchService;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

			//从solr中删除,去耦合
//			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			// 删除商品页面服务器详细页
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}


	/**
	 * 审核成功添加到solr库
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status) {
		try {
			goodsService.updateStatus(ids, status);
			if("1".equals(status)){//审核通过添加solr中
				//得到需要导入sku的列表
				List<TbItem> itemList = goodsService.findItemListByGoodsIdAndStatus(ids, status);

				// 导入solr,解除耦合。使用消息队列
				//itemSearchService.importList(itemList);

				// 转成string方便发送
				final String jsonString = JSON.toJSONString(itemList);

				jmsTemplate.send(queueSolrDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {

						return session.createTextMessage(jsonString);
					}
				});

				// 生成商品详情页
				for(final Long goodsId : ids){
//					itemPageService.genItemHtml(goodsId);
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(goodsId + "");
						}
					});
				}

			}
			return new Result(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "操作失败");
		}
	}
	
}
