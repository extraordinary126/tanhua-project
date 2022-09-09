package com.yuhao.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.bean.Announcement;
import com.yuhao.dubbo.api.AnnouncementApi;
import com.yuhao.mappers.AnnouncementMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DubboService
public class AnnouncementApiImpl implements AnnouncementApi {

    @Autowired
    AnnouncementMapper announcementMapper;

    //获取公告 mysql表 tb_announcement
    @Override
    public List<Announcement> getAnnouncements(Integer page, Integer pagesize) {
        Page pageInfo = new Page(page, pagesize);
        IPage<Announcement> iPage = announcementMapper.getAllAnnouncements(pageInfo);
        return iPage.getRecords();
    }
}
