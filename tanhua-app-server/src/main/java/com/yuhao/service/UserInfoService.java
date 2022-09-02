package com.yuhao.service;

import com.yuhao.VO.ErrorResult;
import com.yuhao.VO.UserInfoVO;
import com.yuhao.bean.UserInfo;
import com.yuhao.dubbo.api.UserInfoApi;
import com.yuhao.exception.BuinessException;
import com.yuhao.tanhua.autoconfig.template.AipFaceTemplate;
import com.yuhao.tanhua.autoconfig.template.OssTemplate;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class UserInfoService {

    @DubboReference
    UserInfoApi userInfoApi;

    @Autowired
    OssTemplate ossTemplate;


    @Autowired
    AipFaceTemplate aipFaceTemplate;

    public void save(UserInfo userInfo){
        userInfoApi.save(userInfo);
    }

    //更新用户头像方法
    public void updateHead(MultipartFile headPhoto, Long id) {
        try {
            //1.上传图片到阿里云OSS
            String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
            //2.调用百度API完成人脸的识别 是人则返回true
            boolean isHuman = aipFaceTemplate.detect(imageUrl);
            if (!isHuman){
                throw new BuinessException(ErrorResult.faceError());
            }else{
                //包含人脸 更新
                UserInfo userInfo = new UserInfo();
                userInfo.setId(Long.valueOf(id));
                userInfo.setAvatar(imageUrl);
                userInfoApi.update(userInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserInfoVO getUserInfo(Long id){
        UserInfo userInfo = userInfoApi.getUserInfo(id);
        UserInfoVO vo = new UserInfoVO();
        //将userinfo 中的值 copy到 vo中  非同名同类型的不会copy
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getAge() != null){
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;
    }

    public void update(UserInfo userInfo) {
        userInfoApi.update(userInfo);
    }
}
