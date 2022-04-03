package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName TagDao.java
 * @Description TODO
 * @createTime 2022年03月18日 22:20:00
 */
@Mapper
public interface TagDao {


    //增
    @Insert("insert into tag(id,name,description,tag)values(#{id},#{name},#{description},#{tag})")
    public void addTag(Tag tag);


    //删
    @Delete("delete from tag where id = #{id}")
    public int delTag(Long id);



    //查tag
    @Select("select * from tag where tag = 1")
    public List<Tag> listTag();

    //查tag2
    @Select("select * from tag where tag = #{tag}")
    public List<Tag>  listTag2(@Param("tag")String tag);
}
