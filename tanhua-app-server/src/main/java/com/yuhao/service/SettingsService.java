package com.yuhao.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuhao.VO.PageResult;
import com.yuhao.VO.SettingsVo;
import com.yuhao.bean.Question;
import com.yuhao.bean.Settings;
import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.BlackListApi;
import com.yuhao.dubbo.api.QuestionApi;
import com.yuhao.dubbo.api.SettingsApi;
import com.yuhao.interceptor.UserThreadLocalHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

    @DubboReference
    private SettingsApi settingsApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private BlackListApi blackListApi;

    @Autowired
    UserService userService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     *      private Long id;
     *     private String strangerQuestion = "";    //用户陌生人问题
     *     private String phone;                    //手机号
     *     private Boolean likeNotification = true;     //陌生人通知开关
     *     private Boolean pinglunNotification = true;      //通知开关
     *     private Boolean gonggaoNotification = true;      //通知开关
     */
    public SettingsVo getSettings(){
        SettingsVo settingsVo = new SettingsVo();
        Long userId = UserThreadLocalHolder.getId();
        //设置id 和手机号
        settingsVo.setId(userId);
        settingsVo.setPhone(UserThreadLocalHolder.getMobile());
        //设置陌生人问题
        Question question = questionApi.getByUserId(userId);
        String txt = (question == null ? "该用户还没有设置对陌生人的问题" : question.getTxt());
        settingsVo.setStrangerQuestion(txt);


        Settings settings = settingsApi.getSettings(userId);
        settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
        settingsVo.setLikeNotification(settings.getLikeNotification());
        settingsVo.setPinglunNotification(settings.getPinglunNotification());

        return settingsVo;
    }

    //设置用户陌生人问题
    public void setQuestions(String content){
        Long id = UserThreadLocalHolder.getId();
        questionApi.setQuestions(content, id);
    }
    //设置用户设置
    public void setNofication(Settings settings) {
        Long userID = UserThreadLocalHolder.getId();
        settings.setUserId(userID);
        settingsApi.setNofication(settings);
    }

    //分页查询黑名单列表
    public PageResult getBlacklist(int page, int pageSize) {
        //1.获取当前用户userID
        Long userID = UserThreadLocalHolder.getId();
        //2.调用api 查询黑命单列表  mybatisplus 分页功能 返回IPage对象
        IPage<UserInfo> iPage = blackListApi.getBlacklistByUserId(userID, page, pageSize);
        //3.将IPage对象转化为 PageResult的VO对象
        PageResult pageResult = new PageResult(page, pageSize, Math.toIntExact(iPage.getTotal()), iPage.getRecords());
        return pageResult;
    }

    //取消黑名单
    public void cancelBlacklist(Long uid) {
        blackListApi.cancelBlackList(uid);
    }

    //修改手机号2 之校验验证码
    public Boolean checkVerificationCode(String verificationCode) {
        String oldpPhoneNumber = UserThreadLocalHolder.getMobile();
        String codeInRedis = redisTemplate.opsForValue().get("CHECK_CODE_" + oldpPhoneNumber);
        if (codeInRedis == null || !(codeInRedis.equals(verificationCode))){
            //没有查到验证码
            return false;
        }
        return true;
    }

    //修改手机号3: 保存新的手机号
    public void updatePhoneNumber(String newPhoneNumber){
        String oldPhoneNumber = UserThreadLocalHolder.getMobile();
        settingsApi.updatePhoneNumber(oldPhoneNumber, newPhoneNumber);
    }
}
