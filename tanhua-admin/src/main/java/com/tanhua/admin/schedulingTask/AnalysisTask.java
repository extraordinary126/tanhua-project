package com.tanhua.admin.schedulingTask;

import com.tanhua.admin.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AnalysisTask {

    @Autowired
    AnalysisService analysisService;

    //定时任务方法
    // 秒 分 时  日 月 周   日和周必须有一个忽略:?
    //每小时执行一次
    @Scheduled(cron = "0 */1 * * * ?")
    public void analysisTask(){
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println(time);

        try {
            analysisService.analysis();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
