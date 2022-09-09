package com.yuhao.service;

import com.alibaba.fastjson.JSON;
import com.yuhao.VO.ErrorResult;
import com.yuhao.VO.PageResult;
import com.yuhao.VO.TodayBest;
import com.yuhao.bean.Mongo.RecommendUser;
import com.yuhao.bean.Question;
import com.yuhao.bean.UserInfo;
import com.yuhao.common.utils.Constants;
import com.yuhao.dto.RecommendUserDto;
import com.yuhao.dubbo.api.QuestionApi;
import com.yuhao.dubbo.api.RecommendUserApi;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.exception.BuinessException;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.tanhua.autoconfig.template.HuanXinTemplate;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @Autowired
    HuanXinTemplate huanXinTemplate;

    public TodayBest getTodayBest() {
        //1.获取当前userId
        Long userId = UserThreadLocalHolder.getId();
        //2.调用api查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        if (recommendUser == null){
            //如果没有推荐的用户 那么设置一个默认值
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(90D);
        }
        //将RecommendUser对象转换为TodayBest  VO对象返回
        //VO对象中包含了UserInfo对象的信息
        // 所以通过RecommendUser对象中的推荐用户的userId查询该用户的UserInfo
        Long recommenderId = recommendUser.getUserId();
        UserInfo userInfo = userInfoApi.getUserInfo(recommenderId);
        //RecommendUser对象转换为TodayBest
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        return vo;
    }
                                    //dto是传入的数据  查出要求符合dto条件的人
    public PageResult recommendation(RecommendUserDto dto) {
        //1.获取当前userId
        Long toUserId = UserThreadLocalHolder.getId();
        //2.调用recommenderUserApi完成分页查询列表   PageResult --> RecomendUser
        PageResult pr = recommendUserApi.getRecommendUserList(dto.getPage(),dto.getPagesize(),toUserId);
        //3.获取分页中的RecommendUser数据列表 items是存查出来的所有该用户的推荐用户
        List<RecommendUser> items = (List<RecommendUser>) pr.getItems();
        //4.判断列表是否为空
        if (items == null){
            //不包含任何的推荐数据
            return pr;
        }
        //以下代码会频繁的进行远程调用 每有一个推荐的用户,就会调用一次dubbo远程服务查询用户信息
        //所以改造代码  调用一次 查出所有与用户关联的推荐用户
        //然后在service层进行过滤和对象的转换

//        //5.循环RecommendUser数据列表,根据推荐的用户ID查询用户详情  并根据条件过滤一下查询出的用户
//        for (RecommendUser recommendUser : items){
//                Long recommendUserId = recommendUser.getUserId();
//                UserInfo userInfo = userInfoApi.getUserInfo(recommendUserId);
//                if (userInfo != null){
//                    if (!StringUtils.isEmpty(dto.getGender()) && !dto.getGender().equals(userInfo.getGender())){
//                        continue;
//                    }
//                    if (dto.getAge() == null && dto.getAge() < userInfo.getAge() ){
//                        continue;
//                    }
//                    TodayBest vo = TodayBest.init(userInfo, recommendUser);
//                    list.add(vo);
//                }
//            }
        //5.i提取所有的推荐用户 id列表
        List<Long> userIdsList = new ArrayList<>();
        for (RecommendUser recommendUser : items) {
            Long userId = recommendUser.getUserId();
            userIdsList.add(userId);
        }
        //6.构建查询条件 批量查询所有的用户详情
        // new 一个 userInfo对象作为userinfoApi批量查询的条件
        UserInfo userInfo = new UserInfo();

        userInfo.setAge(dto.getAge());
        userInfo.setGender(dto.getGender());

        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(userIdsList, userInfo);
        List<TodayBest > returnList = new ArrayList<>();
        //到这里 已经成功将初始的items 转换为 需要的userInfoMap
        //7.循环推荐的数据列表 构建VO对象
        for (RecommendUser recommendUser : items) {
            //从所有推荐用户的数据中取出id
            Long idInAll = recommendUser.getUserId();
            //将取出的id和新构建的id进行匹配,得到userinfo对象
            UserInfo userInfo1 = userInfoMap.get(idInAll);
            //将得到的userinfo对象转换成 recommenderUser对象
            if (userInfo1 != null) {
                TodayBest vo = TodayBest.init(userInfo1, recommendUser);
                returnList.add(vo);
            }
        }
        //8构造返回值
        pr.setItems(returnList);
        return pr;
    }

    //查看佳人信息
    public TodayBest getPersonalInfo(Long userId) {
        Long toUserId = UserThreadLocalHolder.getId();
        UserInfo userInfo = userInfoApi.getUserInfo(userId);
        RecommendUser recommendUser = recommendUserApi.getTwoPeopleScore(userId, toUserId);
        TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
        return todayBest;
    }

    //查询用户的陌生人问题 tb_question表
    public String getStrangerQuestions(Long userId) {
        Question question = questionApi.getByUserId(userId);
        return question == null ? "该用户没有填写陌生人问题"  :  question.getTxt();
    }

    //回复陌生人问题
    //要回复给的用户的id   回复内容
    public void replyStrangerQuestions(Long targetUserId, String reply) {
        // 1. 构造消息数据(JSON)
        Long userId = UserThreadLocalHolder.getId();

        Map<String, Object> map = new HashMap<>();
        UserInfo userInfo = userInfoApi.getUserInfo(userId);
        map.put("userId", userId);
        map.put("huanXinId", Constants.HX_USER_PREFIX + userId);
        map.put("nickname", userInfo.getNickname());
        map.put("strangerQuestion", getStrangerQuestions(targetUserId));
        map.put("reply", reply);
        String jsonMessage = JSON.toJSONString(map);
        //2. 调用template 通过环信完成消息的发送(加好友的消息)
        //username: 接收方的用户id    , content: 发送的消息
        Boolean flag = huanXinTemplate.sendMsg(Constants.HX_USER_PREFIX + targetUserId, jsonMessage);
        if (!flag){
            throw new BuinessException(ErrorResult.error());
        }
    }
}
