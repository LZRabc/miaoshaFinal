package com.geekq.miaosha.controller;

import com.geekq.miaosha.common.resultbean.ResultGeekQ;
import com.geekq.miaosha.domain.Tag;
import com.geekq.miaosha.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName TagController.java
 * @Description TODO
 * @createTime 2022年03月18日 22:37:00
 */
@Controller
public class TagController {
    @Autowired
    private TagService tagService;


    @PostMapping("/addTag")
    @ResponseBody
    public ResultGeekQ<Tag> addTag(@RequestBody Tag tag){
        ResultGeekQ<Tag> result =  ResultGeekQ.build();
        tagService.addTag(tag);
        result.success();
        return result;
    }

    @GetMapping("/getTag")
    @ResponseBody
    public ResultGeekQ<List<Tag>> listTag(){
        ResultGeekQ<List<Tag>> result = ResultGeekQ.build();
        List<Tag> tag = tagService.getTag();
        result.setData(tag);
        result.success();
        return result;
    }
    @GetMapping("/getTag2")
    @ResponseBody
    public ResultGeekQ<List<Tag>> listTag2(@RequestParam("tag") String tag){
        ResultGeekQ<List<Tag>> result = ResultGeekQ.build();
        List<Tag> tags = tagService.getTag2(tag);
        result.setData(tags);
        result.success();
        return result;
    }
}
