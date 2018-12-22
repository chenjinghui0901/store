package com.store.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.store.pojo.TbBrand;
import com.store.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {


    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int page, int size){
        return brandService.findPage(page, size);
    }

    @RequestMapping("/save")
    public Result add(@RequestBody TbBrand brand){
        String message = "";
        try {
            if (brand.getId() == null) {
                message = "增加";
                brandService.add(brand);
            } else {
                message = "修改";
                brandService.update(brand);
            }
            return new Result(true, message + "成功");
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false, message + "失败");
        }
    }

    @RequestMapping("/findById")
    public TbBrand findById(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand, int page, int size){
        return brandService.findPage(brand, page, size);
    }

    /*@RequestMapping("update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true, "修改成功");
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }*/

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
