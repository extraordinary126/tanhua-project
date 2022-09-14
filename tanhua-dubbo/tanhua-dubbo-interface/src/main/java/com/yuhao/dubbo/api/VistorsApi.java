package com.yuhao.dubbo.api;

import com.yuhao.bean.Mongo.Visitors;

import java.util.List;

public interface VistorsApi {

    //保存访客数据
    void save(Visitors visitors);

    //查询访客列表
    List<Visitors> getVisitorsList(Long date, Long userId);
}
