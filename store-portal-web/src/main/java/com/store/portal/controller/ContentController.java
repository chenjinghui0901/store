package com.store.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.store.content.service.ContentService;
import com.store.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId) {
        return contentService.findByCategoryId(categoryId);
    }

}
