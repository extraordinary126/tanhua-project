package com.tanhua.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.bean.Admin;


public interface AdminMapper extends BaseMapper<Admin> {
    Admin selectByUserName(String username);
}
