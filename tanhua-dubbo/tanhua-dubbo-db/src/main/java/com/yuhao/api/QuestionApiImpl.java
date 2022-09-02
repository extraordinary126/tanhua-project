package com.yuhao.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yuhao.bean.Question;
import com.yuhao.dubbo.api.QuestionApi;
import com.yuhao.mappers.QuestionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class QuestionApiImpl implements QuestionApi {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question getByUserId(Long userId) {
        LambdaQueryWrapper<Question> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Question::getUserId, userId);
        Question question = questionMapper.selectOne(lambdaQueryWrapper);
        return question;
    }

    @Override
    public void setQuestions(String content, Long id) {
        Question question = new Question();
        question.setTxt(content);
        question.setUserId(id);
        LambdaUpdateWrapper<Question> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Question::getUserId, id);
        int update = questionMapper.update(question, lambdaUpdateWrapper);
        if (update == 0){
            questionMapper.insert(question);
        }
    }
}
