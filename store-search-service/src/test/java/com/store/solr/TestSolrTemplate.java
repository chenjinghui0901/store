package com.store.solr;

import com.store.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/solr-test.xml")
public class TestSolrTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void testAdd() {
        TbItem TbItem = new TbItem();
        TbItem.setId(1L);
        TbItem.setBrand("华为");
        TbItem.setCategory("手机");
        TbItem.setGoodsId(1L);
        TbItem.setSeller("华为2号专卖店");
        TbItem.setTitle("华为Mate10");
        TbItem.setPrice(new BigDecimal(2000));
        solrTemplate.saveBean(TbItem);
        solrTemplate.commit();
    }

    @Test
    public void testFindOne() {
        TbItem TbItem = solrTemplate.getById(1, TbItem.class);
        System.out.println(TbItem.getUpdateTime());
    }

    /**
     * 根据id删除
     */
    @Test
    public void testDeleteById() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test
    public void testAddList() {
        List<TbItem> list = new ArrayList();

        for (int i = 0; i < 100; i++) {
            TbItem TbItem = new TbItem();
            TbItem.setId(i + 1L);
            TbItem.setBrand("华为");
            TbItem.setCategory("手机" + i);
            TbItem.setGoodsId(1L);
            TbItem.setSeller("华为" + i + "号专卖店");
            TbItem.setTitle("华为Mate" + i);
            TbItem.setUpdateTime(new Date());
            TbItem.setPrice(new BigDecimal(2000 + i));
            list.add(TbItem);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /**
     * 分页查询
     */
    @Test
    public void testPageQuery(){
        Query query=new SimpleQuery("*:*");
        query.setOffset(20);//开始索引（默认0）
        query.setRows(20);//每页记录数(默认10)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数："+page.getTotalElements());
        List<TbItem> list = page.getContent();
        showList(list);
    }

    private void showList(List<TbItem> list){
        for(TbItem TbItem:list){
            System.out.println(TbItem.getTitle() +TbItem.getPrice());
        }
    }

    /**
     * 分页条件
     */
    @Test
    public void testPageQueryMutil(){
        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_title").contains("2");
        criteria=criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        //query.setOffset(20);//开始索引（默认0）
        //query.setRows(20);//每页记录数(默认10)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数："+page.getTotalElements());
        List<TbItem> list = page.getContent();
        showList(list);
    }


    /**
     * 删除所有
     */
    @Test
    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


}
