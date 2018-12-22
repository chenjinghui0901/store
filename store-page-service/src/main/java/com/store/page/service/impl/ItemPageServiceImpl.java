package com.store.page.service.impl;

import com.store.mapper.TbGoodsDescMapper;
import com.store.mapper.TbGoodsMapper;
import com.store.mapper.TbItemCatMapper;
import com.store.mapper.TbItemMapper;
import com.store.page.service.ItemPageService;
import com.store.pojo.TbGoods;
import com.store.pojo.TbGoodsDesc;
import com.store.pojo.TbItem;
import com.store.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService{

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("$pagedir")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            // 创建数据模型
            Map dataModel = new HashMap<>();
            // 商品主表
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            // 商品扩展表
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            // 商品分类
            String itemCat1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            // sku列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);// spu id
            criteria.andStatusEqualTo("1");// 状态有效
            example.setOrderByClause("is_default_desc");//是否默认，第一条为默认sku
            List<TbItem> itemList = itemMapper.selectByExample(example);

            dataModel.put("goods", goods);
            dataModel.put("goodsDesc", goodsDesc);
            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);
            dataModel.put("itemList", itemList);

            Writer out = new FileWriter(pagedir + goodsId + ".html");
            template.process(dataModel, out);
            out.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for(Long goodsId : goodsIds){
                new File(pagedir + goodsId + ".html").delete();
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


}
