package com.yuhao.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.bean.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserInfoMapper extends BaseMapper<UserInfo> {

//    @Select("SELECT b.black_user_id, u.nickname, u.avatar, u.id,  u.gender, u.age\n" +
//            "FROM tb_black_list as b\n" +
//            "INNER JOIN tb_user_info as u\n" +
//            "WHERE b.black_user_id = u.id and b.user_id = #{userID}} GROUP BY b.black_user_id;")
    @Select("SELECT b.black_user_id, u.nickname, u.avatar, u.id, u.gender, u.age\n" +
        "FROM tb_black_list as b\n" +
        "INNER JOIN tb_user_info as u\n" +
        "WHERE b.black_user_id = u.id and b.user_id = #{userID} GROUP BY b.black_user_id")
    IPage<UserInfo> getBlackListPage(@Param("pageInfo") Page pageInfo,@Param("userID") Long userID);

    @Update("UPDATE tb_user_info SET nickname = #{nickname} WHERE id = #{id}")
    Boolean updateNickname(@Param("id") Long id , @Param("nickname") String nickname);
}
