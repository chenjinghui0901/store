package com.store.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.store.mapper.*;
import com.store.pojo.*;
import com.store.pojo.TbGoodsExample.Criteria;
import com.store.pojo.group.Goods;
import com.store.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;//SPU

	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;//SKU

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private TbBrandMapper brandMapper;

	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		goods.getGoods().setIsMarketable("1");//默认上架
		goods.getGoods().setAuditStatus("0");//状态：未审核
		goodsMapper.insert(goods.getGoods());//插入商品基本信息

		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//将商品基本表的ID给商品扩展表
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展表数据

		saveItemList(goods);
	}

	/**
	 * 保存sku列表数据，item
	 * @param goods
	 */
	private void saveItemList(Goods goods){
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			for(TbItem item:   goods.getItemList()){
				//构建标题  SPU名称+ 规格选项值
				String title=goods.getGoods().getGoodsName();//SPU名称
				Map<String,Object> map=  JSON.parseObject(item.getSpec());
				for(String key:map.keySet()) {
					title+=" "+map.get(key);
				}
				item.setTitle(title);

				setItemValues(item,goods);

				itemMapper.insert(item);
			}
		}else{//没有启用规格

			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//标题
			item.setPrice(goods.getGoods().getPrice());//价格
			item.setNum(99999);//库存数量
			item.setStatus("1");//状态
			item.setIsDefault("1");//默认
			item.setSpec("{}");//规格

			setItemValues(item,goods);

			itemMapper.insert(item);
		}
	}

	private void setItemValues(TbItem item,Goods goods){
		//商品分类
		item.setCategoryid(goods.getGoods().getCategory3Id());//三级分类ID
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//更新日期

		item.setGoodsId(goods.getGoods().getId());//商品ID
		item.setSellerId(goods.getGoods().getSellerId());//商家ID

		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		item.setBrand(brand.getName());
		//商家名称(店铺名称)
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(seller.getNickName());

		//图片
		List<Map> imageList = JSON.parseArray( goods.getGoodsDesc().getItemImages(), Map.class) ;
		if(imageList.size()>0){
			item.setImage( (String)imageList.get(0).get("url"));
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		// 更新基本表信息
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		// 更新扩展表信息
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		// 更新sKu信息
		//先删除原有在添加
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);

		saveItemList(goods);

	}
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		// 基本信息
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		// 商品扩展表
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(goodsDesc);

		// 读取sku列表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 批量删除spu
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
//			goodsMapper.deleteByPrimaryKey(id);
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");//逻辑删除
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		criteria.andIsDeleteIsNull();//指定条件未逻辑删除
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

	/**
	 * 根据spu的id集合查询sku列表（spu 1 : sku n)
	 * @param goodsid
	 * @param status
	 * @return
	 */
	@Override
	public List<TbItem> findItemListByGoodsIdAndStatus(Long[] goodsid, String status) {

		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);// 状态
		criteria.andGoodsIdIn(Arrays.asList(goodsid));
		return itemMapper.selectByExample(example);
	}

}
