package com.yuhao.service;

import com.yuhao.VO.ErrorResult;
import com.yuhao.VO.MovementsVo;
import com.yuhao.VO.PageResult;
import com.yuhao.bean.Mongo.Movement;
import com.yuhao.bean.UserInfo;
import com.yuhao.common.utils.Constants;
import com.yuhao.dubbo.api.MomentApi;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.exception.BuinessException;
import com.yuhao.interceptor.UserThreadLocalHolder;
import com.yuhao.tanhua.autoconfig.template.OssTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MomentService {

    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MomentApi momentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    // private StringRedisTemplate  redisTemplate;   只能存取kv都是 String类型的
    //private RedisTemplate  redisTemplate;  错误的  取的时候必报错

    //发布动态
    public void sendMoment(Movement movement, MultipartFile[] multipartFiles) throws IOException {
        //1.判断发布动态的内容是否存在
        if (StringUtils.isEmpty(movement.getTextContent())) {
            throw new BuinessException(ErrorResult.contentError());
        }
        //2.获取当前登录的id
        Long userId = UserThreadLocalHolder.getId();
        //3.将文件上传到阿里云OSS,获取请求地址
        List<String> medias = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String path = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
            medias.add(path);
        }
        //4.封装Movement对象
        movement.setUserId(userId);
        movement.setMedias(medias);
        //5. 调用api发布动态
        momentApi.sendMomoent(movement);
    }

    //查询我的所有动态  PageResult包含数据列表 和 分页相关的数据 api层也可以只返回数据列表list就行
    public PageResult getMoment(Long userId, Integer page, Integer pagesize) {
        //1.根据userId, 调用API查询用户个人对象 返回PageResult对象 (包含Movement对象)
        PageResult pageResult = momentApi.getMoment(userId, page, pagesize);
        //2.获取PageResult中的item 对象
        List<Movement> items = (List<Movement>) pageResult.getItems();
        //3.判断是否为空 是否有数据
        if (items == null) {
            return pageResult;
        }
        //4.循环数据列表
        List<MovementsVo> voList = new ArrayList<>();
        //因为查询的是我的动态 所以用户信息就是我 只需要查询一次
        UserInfo userInfo = userInfoApi.getUserInfo(userId);
        for (Movement movement : items) {
            //5.一个Movement 构建一个VO对象
            //将userInfo和movoement 融合转换到VO对象
            MovementsVo movementsVo = MovementsVo.init(userInfo, movement);
            voList.add(movementsVo);
        }
        //6.构建返回值
        pageResult.setItems(voList);
        return pageResult;
    }

    //查询好友动态 分页
    // PageResult包含数据列表 和 分页相关的数据 API层也可以只返回数据列表list就行
    public PageResult getFriendMoment(Integer page, Integer pagesize) {
        //1.获取当前用户id
        Long userId = UserThreadLocalHolder.getId();
        //2.调用api查询用户id的好友发布的动态列表
        List<Movement> movementList = momentApi.getFriendMoment(page, pagesize, userId);
        return getPageResult(page, pagesize, movementList);
    }


    //抽取出的方法 接上面那个
    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> movementList) {
        //3.判断动态列表是否为空
        if (movementList == null || movementList.size() == 0) {
            // 空的pageResult 里面都是默认值
            return new PageResult();
        }
        //4.提取动态发布人的id列表
        List<Long> idList = new ArrayList<>();
        for(Movement movement : movementList){
            Long userId1 = movement.getUserId();
            idList.add(userId1);
        }
        //5.根据用户的id获取用户详情
        //以前写过的api 根据list集合里的id查询 第二个参数是查询条件的封装
        Map<Long, UserInfo> userInfoMap = userInfoApi.getUserInfoMap(idList, null);
        //6.movement对象里没有用户信息 所以将 一个movement对象就构建一个VO对象
        List<MovementsVo> voList = new ArrayList<>();
        for (Movement movement : movementList){
            UserInfo userInfo = userInfoMap.get(movement.getUserId());
            if (userInfo != null){
                //转换成vo对象 缝合userInfo 和动态对象movement
                MovementsVo movementsVo = MovementsVo.init(userInfo, movement);

                //Redis的hash 是一个string类型的key和value的映射表，这里的value是一系列的键值对，hash特别适合用于存储对象。
                //哈希类型的数据操作总的思想是通过key和field操作value，key是数据标识，field是域，value是我们感
                //兴趣的业务数据。

                //修复点赞状态的bug，判断hashKey是否存在
                String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
                String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserThreadLocalHolder.getId();
                if(redisTemplate.opsForHash().hasKey(key,hashKey)) {
                    movementsVo.setHasLiked(1);
                }
                String loveHashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserThreadLocalHolder.getId();
                if (redisTemplate.opsForHash().hasKey(key, loveHashKey)){
                    movementsVo.setHasLoved(1);
                }

                voList.add(movementsVo);
            }
        }
        //7.构建PageResult对象返回
        return new PageResult(page, pagesize, 0 , voList);
    }

    //查询推荐动态
    public PageResult getRecommendMonments(Integer page, Integer pagesize) {
        //推荐动态的内容拼接到了redis  如果redis里没有推荐动态 就随机生成动态pid展示
        //1.从redis中获取推荐数据
        String redisKey = Constants.MOVEMENTS_RECOMMEND + UserThreadLocalHolder.getId();
        //2.判断数据是否存在
        String redisValue = (String) redisTemplate.opsForValue().get(redisKey);

        //3.如果不存在 调用api 随机生成10条动态数据
        List<Movement> list = new ArrayList<>();
        if (StringUtils.isEmpty(redisValue)){
            list = momentApi.randomMovements(pagesize);
        }else {
            //4.存在,处理pid数据
            //redis 数据: "12,32,45,67,112,106,121,126,10011,10012"
            //根据page 和 pagesize 将pid 进行一个手动的分页处理
            String[] values = redisValue.split(",");
            //判断当前页的起始条数是否小于数组总数
            if (values.length > (page - 1) * pagesize){
                //使用stream流 来过滤数据
                List<Long> pidsList = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize)
                        //将数据转换为long类型
                        .map(s -> Long.valueOf(s))
                        //收集数据为list
                        .collect(Collectors.toList());
                //5.调用api 通过pid数组进行动态的查询
                list = momentApi.getMomentByPidsList(pidsList);
            }
        }
        //6.调用刚才抽取的方法 传入动态的List  返回构建好的PageResult(里面有动态MovementsVO对象)
        return getPageResult(page, pagesize, list);
    }

    //查询单挑动态
    public MovementsVo getSingleMoment(String id) {
        Movement movement = momentApi.getSingleMoment(id);
        if (movement != null) {
            UserInfo userInfo = userInfoApi.getUserInfo(movement.getUserId());
            return MovementsVo.init(userInfo, movement);
        }
        return null;
    }
}
