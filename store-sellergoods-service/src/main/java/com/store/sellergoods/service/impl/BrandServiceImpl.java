package com.store.sellergoods.service.impl;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.store.mapper.TbBrandMapper;
import com.store.pojo.TbBrand;
import com.store.pojo.TbBrandExample;
import com.store.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService{

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize); // 分页
        TbBrandExample example = new TbBrandExample();
        if(brand != null){
            TbBrandExample.Criteria criteria = example.createCriteria();
            if(StringUtils.isNotBlank(brand.getFirstChar())){

                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
            }
            if(StringUtils.isNotBlank(brand.getName())){
                criteria.andNameLike("%" + brand.getName() + "%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 返回下拉列表
     * @return
     */
    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }


}
