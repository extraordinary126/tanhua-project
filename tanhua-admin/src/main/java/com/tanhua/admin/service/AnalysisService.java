package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.admin.mapper.AnalysisMapper;
import com.tanhua.admin.mapper.LogMapper;
import com.yuhao.bean.Analysis;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class AnalysisService {

    @Resource
    private LogMapper logMapper;

    @Resource
    private AnalysisMapper analysisMapper;

    //定时统计tb_log表中的数据  插入或者更新
    //1.查询tb_log 表
    //2.构造Analysis对象
    //3.保存或者更新
//    0101为登录，0102为注册，
//            * 0201为发动态，0202为浏览动态，0203为动态点赞，0204为动态喜欢，0205为评论，0206为动态取消点赞，0207为动态取消喜欢，
//            * 0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
    public void analysis() throws ParseException {
        //定义查询日期
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String yesterday = DateUtil.yesterday().toString("yyyy-MM-dd");
        //统计数据  注册数量
        Integer registryCount = logMapper.queryByTypeAndLogTime("0102", today);
        //统计数据  登录数量
        Integer loginCount = logMapper.queryByTypeAndLogTime("0101", today);
        //统计数据  活跃数量
        Integer activeCount = logMapper.queryByLogTime(today);
        //统计数据  次日留存
        Integer newCount = logMapper.queryNumRetention1d(today, yesterday);

        //根据日期查询数据
        LambdaQueryWrapper<Analysis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Analysis::getRecordDate, new SimpleDateFormat("yyyy-MM-dd").parse(today));
        Analysis oldAnalysis = analysisMapper.selectOne(queryWrapper);
        //如果存在 更新  如果不存在 保存
        if (oldAnalysis == null){
            Analysis analysis = new Analysis();
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRegistered(registryCount);
            analysis.setNumRetention1d(newCount);
            analysis.setCreated(new Date());
            analysis.setRecordDate(new Date());
            analysisMapper.insert(analysis);
        }else {
            // 查到了值 更新
            oldAnalysis.setNumLogin(loginCount);
            oldAnalysis.setNumActive(activeCount);
            oldAnalysis.setNumRegistered(registryCount);
            oldAnalysis.setNumRetention1d(newCount);
            oldAnalysis.setRecordDate(new SimpleDateFormat("yyyy-MM-dd").parse(today));
            analysisMapper.updateById(oldAnalysis);
        }
    }


}
