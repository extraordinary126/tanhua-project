package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuhao.VO.MovementsVo;
import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.Movement;
import com.yuhao.bean.UserInfo;
import com.yuhao.common.utils.Constants;
import com.yuhao.dubbo.api.MomentApi;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.dubbo.api.VideoApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MomentApi momentApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //查询用户列表
    public PageResult findAllUsers(Integer page, Integer pagesize) {
        //分页查询所有用户的id
        IPage<UserInfo> ipage = userInfoApi.getAll(page, pagesize);
        List<UserInfo> list = ipage.getRecords();
        for (UserInfo userInfo : list) {
            String key = Constants.USER_FREEZE + userInfo.getId();
            if (redisTemplate.hasKey(key)){
                userInfo.setUserStatus("2");
            }
        }
        return new PageResult(page, pagesize, Math.toIntExact(ipage.getTotal()), list);
    }

    //查询单个用户信息
    public ResponseEntity findById(Long userId) {
        UserInfo userInfo = userInfoApi.getUserInfo(userId);
        String key = Constants.USER_FREEZE + userId;
        if (redisTemplate.hasKey(key)){
            userInfo.setUserStatus("2");
        }
        return ResponseEntity.ok(userInfo);
    }

    //获取用户视频列表
    public PageResult getVideosList(Integer page, Integer pagesize, Long uid) {

        PageResult pageResult = videoApi.getVideoListById(page, pagesize, uid);

        return pageResult;
    }

    //根据id查看用户动态
    public PageResult findAllMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        //根据api  查询id 和 state 对应的Movement列表
        PageResult result = momentApi.getMomentByIdAndState(uid, state, page, pagesize);
        List<Movement> movementList = (List<Movement>) result.getItems();
        if (CollUtil.isEmpty(movementList)) {
            return new PageResult();
        }
        //因为查询的都是同一个用户
        List<Long> userIdList = CollUtil.getFieldValues(movementList, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(userIdList, null);
        List<MovementsVo> voList = new ArrayList<>();
        for (Movement movement : movementList) {
            UserInfo userInfo = userInfoMap.get(movement.getUserId());
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            voList.add(vo);
        }
        return new PageResult(page, pagesize, result.getCounts(), voList);
    }

    public MovementsVo getMomentDetail(String movementId) {
        Movement moment = momentApi.getSingleMoment(movementId);
        Long userId = moment.getUserId();

        UserInfo userInfo = userInfoApi.getUserInfo(userId);
        return MovementsVo.init(userInfo, moment);
    }

    //用户冻结
    public Map userFreeze(Map params) {
        //1.构造key
        String userId = params.get("userId").toString();
        String key = Constants.USER_FREEZE + userId;
        //2.构造失效时间
        Integer freezingTime = Integer.valueOf((String) params.get("freezingTime"));
        int days = 0;
        if (freezingTime == 1){
            days = 3;
        }else if (freezingTime == 2){
            days = 7;
        }
        //3.存入redis中
        String value = JSON.toJSONString(params);
        if (days > 0){
            redisTemplate.opsForValue().set(key, value, Duration.ofDays(days));
        }else {
            redisTemplate.opsForValue().set(key, value);
        }
        Map retMap = new HashMap();
        retMap.put("message","冻结成功!");
        return retMap;
    }

    //用户解冻
    public Map userUnfreeze(Map params) {
        //拼接出 redis key 删除即可
        String userId = params.get("userId").toString();
        String key = Constants.USER_FREEZE + userId;
        redisTemplate.delete(key);
        Map retMap = new HashMap();
        retMap.put("message","解冻成功!");
        return retMap;
    }
}
