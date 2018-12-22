package com.store.sellergoods.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.store.mapper.TbSpecificationOptionMapper;
import com.store.mapper.TbTypeTemplateMapper;
import com.store.pojo.TbSpecificationOption;
import com.store.pojo.TbSpecificationOptionExample;
import com.store.pojo.TbTypeTemplate;
import com.store.pojo.TbTypeTemplateExample;
import com.store.pojo.TbTypeTemplateExample.Criteria;
import com.store.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}


	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbTypeTemplateExample example = new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();

		if (typeTemplate != null) {
			if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
				criteria.andNameLike("%" + typeTemplate.getName() + "%");
			}
			if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
				criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
			}
			if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
				criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
			}
			if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
				criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
			}

		}

		//加缓存
		saveToRedis();

		Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	//将品牌列表和规格列表放入缓存
	private void saveToRedis(){
		List<TbTypeTemplate> templateList = findAll();
		for (TbTypeTemplate typeTemplate : templateList){
			// 得到品牌列表
			List brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);

			//得到规格列表
			List<Map> specList = findSpecList(typeTemplate.getId());
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
		System.out.println("缓存品牌列表+规格列表");
	}

	@Override
	public List<Map> findSpecList(Long id) {
		// 查询模板
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		// 查询所有规格
		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
		for (Map map : list) {
			// 查询所有规格对应的选项列表
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
			map.put("options", options);
		}
		return list;
	}


}
