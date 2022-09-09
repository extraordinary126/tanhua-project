package com.yuhao.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement extends BasePojo implements Serializable {
    //通知表 对应mysql tb_announcement

    private String id;

    private String title;

    private String description;

//    private String created;
//
//    private String updated;
}
