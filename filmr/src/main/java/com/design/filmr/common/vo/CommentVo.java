package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVo extends Comment {

    // film表信息
    private String filmName;
    // comment_audit表信息
    private String status;

    public CommentVo(Comment comment){
        super(comment);
    }
}
