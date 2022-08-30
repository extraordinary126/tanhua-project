package com.yuhao.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yuhao.bean.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO extends BasePojo {

    //VO对象 用来解决 UserInfo对象的数据类型和前端要求不一直的问题
    //此处的age 是string类型

    private Long id; //用户id
    private String nickname; //昵称
    private String avatar; //用户头像
    private String birthday; //生日
    private String gender; //性别

    private String age; //年龄

    private String city; //城市
    private String income; //收入
    private String education; //学历
    private String profession; //行业
    private Integer marriage; //婚姻状态
    private String tags; //用户标签：多个用逗号分隔
    private String coverPic; // 封面图片

    //用户状态,1为正常，2为冻结
    @TableField(exist = false)
    private String userStatus = "1";
}
