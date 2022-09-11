package com.yuhao.api;

import cn.hutool.core.collection.CollUtil;
import com.yuhao.bean.Mongo.UserLocation;
import com.yuhao.dubbo.api.UserLocationApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@DubboService
public class UserLocationApiImpl implements UserLocationApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    //更新地理位置
    @Override
    public Boolean updateLocation(Long id, Double longitude, Double latitude, String address) {
       try {
           //1.根据用户id查询地理位置信息
           Query query = Query.query(Criteria.where("userId").is(id));
           UserLocation location = mongoTemplate.findOne(query, UserLocation.class);
           //2.如果不存在用户位置信息,保存
           if (location == null) {
               location = new UserLocation();
               location.setUserId(id);
               location.setAddress(address);
               location.setCreated(System.currentTimeMillis());
               location.setUpdated(System.currentTimeMillis());
               location.setLastUpdated(System.currentTimeMillis());
               location.setLocation(new GeoJsonPoint(longitude, latitude));
               mongoTemplate.save(location);
               return true;
           }else {
               //3.如果存在,更新
               Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                       .set("updated", System.currentTimeMillis())
                       .set("lastUpdated", location.getUpdated());
               mongoTemplate.updateFirst(query, update, UserLocation.class);
               return true;
           }
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
    }

    @Override
    public List<Long> getNearUser(Long id, Double meter) {
        //1.根据用户id  查询用户位置信息
        Query query = Query.query(Criteria.where("userId").is(id));
        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
        if (userLocation == null){
            return null;
        }
        //2.以当前用户位置绘制原点=
        GeoJsonPoint geoJsonPoint = userLocation.getLocation();
        //3.绘制半径
        Distance distance = new Distance(meter / 1000, Metrics.KILOMETERS);
        //4.绘制圆
        Circle circle = new Circle(geoJsonPoint, distance);
        //5.查询
        Query query1 = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> userLocations = mongoTemplate.find(query1, UserLocation.class);
        //GeoJsonPoint 不支持序列化
        //提取出id
        List<Long> list = CollUtil.getFieldValues(userLocations, "userId", Long.class);
        return list;
    }
}
