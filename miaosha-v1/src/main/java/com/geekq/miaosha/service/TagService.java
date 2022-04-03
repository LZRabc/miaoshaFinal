package com.geekq.miaosha.service;

import com.geekq.miaosha.common.enums.ResultStatus;
import com.geekq.miaosha.dao.TagDao;
import com.geekq.miaosha.domain.Tag;
import com.geekq.miaosha.exception.GlobleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName tagService.java
 * @Description TODO
 * @createTime 2022年03月18日 22:27:00
 */
@Service
public class TagService {
    @Autowired
    private TagDao tagDao;
    /**
     * @title 添加分类
     * @description
     * @author zzh
     * @updateTime 2022/3/18 22:28
     * @throws
     */
    @Transactional
    public Tag addTag( @RequestBody Tag tag){
        if (tag==null){
            throw new GlobleException(ResultStatus.TYPE_NOT_EMPTY);
        }
        Long id = System.currentTimeMillis();
        tag.setId(id);
        if (tag.getTag()==null){
            tag.setTag("1");
        }
        tagDao.addTag(tag);
        return tag;
    }

    public List<Tag> getTag(){
        List<Tag> tagList = tagDao.listTag();
        return tagList;
    }

    public List<Tag> getTag2(String tag){
        List<Tag> tagList = tagDao.listTag2(tag);
        return tagList;
    }
}
