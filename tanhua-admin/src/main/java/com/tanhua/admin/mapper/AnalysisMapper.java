package com.tanhua.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.bean.Analysis;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


public interface AnalysisMapper extends BaseMapper<Analysis> {

    @Select("select sum(${column}) from tb_analysis where record_date > #{leDate} and record_date < #{gtDate}")
    Long sumAnalysisData(@Param("column") String column, @Param("leDate") String leDate, @Param("gtDate") String gtDate);

}
