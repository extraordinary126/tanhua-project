package com.yuhao.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementVo {

    private String id;

    private String title;

    private String description;

    private String createDate;
}
