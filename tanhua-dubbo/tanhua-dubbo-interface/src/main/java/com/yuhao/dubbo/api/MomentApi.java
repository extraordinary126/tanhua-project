package com.yuhao.dubbo.api;

import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.Movement;

import java.util.List;

public interface MomentApi {

    //发布动态
    String sendMomoent(Movement movement);

    //查询我的所有动态
    PageResult getMoment(Long userId, Integer page, Integer pagesize);

    //根据用户id查询用户好友发布的所有动态
    List<Movement> getFriendMoment(Integer page, Integer pagesize, Long userId);

    //根据pid 数组查询动态
    List<Movement> getMomentByPidsList(List<Long> pidsList);

    //随机生成动态
    List<Movement> randomMovements(Integer counts);

    //查看单条动态
    Movement getSingleMoment(String id);

    //根据id 和 state查询动态
    PageResult getMomentByIdAndState(Long uid, Integer state, Integer page, Integer pagesize);

    //更新动态状态
    void update(String movementId, Integer state);
}
