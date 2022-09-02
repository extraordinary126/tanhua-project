package com.yuhao.dubbo.api;

import com.yuhao.bean.Question;

public interface QuestionApi {

    Question getByUserId(Long userId);

    void setQuestions(String content, Long id);
}
