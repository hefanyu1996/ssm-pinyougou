package cn.itcast.web.controller;

import cn.itcast.pojo.TbContent;
import cn.itcast.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("/findByCategoryId.do")
    public List<TbContent> findByCategoryId(Long categoryId){

         return contentService.findByCategoryId(categoryId);
    }

}
