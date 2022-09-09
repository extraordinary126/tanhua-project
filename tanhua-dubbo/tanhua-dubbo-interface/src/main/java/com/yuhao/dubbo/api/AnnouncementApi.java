package com.yuhao.dubbo.api;

import com.yuhao.bean.Announcement;

import java.util.List;

public interface AnnouncementApi {

    //获取公告 mysql表 tb_announcement
    List<Announcement> getAnnouncements(Integer page, Integer pagesize);
}
