package com.yuhao;

import com.yuhao.bean.Mongo.Comment;
import com.yuhao.dubbo.api.CommentApi;
import com.yuhao.enums.CommentType;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class CommentApiTest {

    @DubboReference
    private CommentApi commentApi;

    @Test
    public void testSave() {
        Comment comment = new Comment();
        comment.setCommentType(CommentType.COMMENT.getType());
        comment.setUserId(1l);
        comment.setCreated(System.currentTimeMillis());
        comment.setContent("测试评论");
        comment.setPublishId(new ObjectId("5e82dc3e6401952928c211a3"));
        commentApi.saveComment(comment);
    }
}