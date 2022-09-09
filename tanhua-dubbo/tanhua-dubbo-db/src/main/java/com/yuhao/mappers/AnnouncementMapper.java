package com.yuhao.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.bean.Announcement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AnnouncementMapper extends BaseMapper<Announcement> {

    @Select("SELECT id,title,description,created FROM tb_announcement ORDER BY CREATED DESC")
    IPage<Announcement> getAllAnnouncements(@Param("pageInfo") Page pageInfo);
}
