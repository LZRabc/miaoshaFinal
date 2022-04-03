package com.geekq.miaosha.dao;

import com.geekq.miaosha.domain.OverDueRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import javax.annotation.ManagedBean;
import java.util.List;

/**
 * @author zzh
 * @version 1.0.0
 * @ClassName OverRecordDao.java
 * @Description TODO
 * @createTime 2022年04月02日 20:25:00
 */
@Mapper
public interface OverRecordDao {

    @Select("select * from overdue_record where user_id = #{id}")
    public List<OverDueRecord> listOverDueRecords (Long id);
}
