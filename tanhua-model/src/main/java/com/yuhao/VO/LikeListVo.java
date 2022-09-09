package com.yuhao.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeListVo implements Serializable {

    private String id;

    private String avatar;

    private String nickname;

    private String createDate;

}
