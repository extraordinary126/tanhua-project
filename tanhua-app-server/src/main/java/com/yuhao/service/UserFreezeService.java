package com.yuhao.service;

import com.alibaba.fastjson.JSON;
import com.yuhao.VO.ErrorResult;
import com.yuhao.common.utils.Constants;
import com.yuhao.exception.BuinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserFreezeService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 判断用户是否已经被冻结, 如果冻结,抛出异常
     *  冻结范围(登录,发布动态,评论),用户id
     *  key: USER_FREEZE_2
     *  value:
     *  {
     *   "userId": 2,
     *   "freezingTime": "1",
     *   "freezingRange": "1",
     *   "reasonsForFreezing": "内容被举报",
     *   "frozenRemarks": ""
     * }
     */
    //state: 1禁止登录   2禁止发言 3禁止发布状态
    public void checkUserStatus(String state, Long userId){
        //1.拼接key
        String key = Constants.USER_FREEZE + userId;
        //2.如果冻结范围一致 且用户id相同 抛出异常
        String value = redisTemplate.opsForValue().get(key);
        if (value == null){
            //没有从redis取到数据 说明没有被冻结
            return;
        }
        Map map = JSON.parseObject(value, Map.class);
        String freezingRange = (String) map.get("freezingRange");
        if (state.equals(freezingRange)){
            throw new BuinessException(ErrorResult.builder().errMessage("用户被冻结中!").build());
        }
    }
}
