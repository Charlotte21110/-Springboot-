package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.CommentUpload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadCommentVo extends CommentUpload {
    // comment_audit表数据
    private Integer commentId;
    private String status;
    private String auditInfo;

    // comment表
    private String comment;
    private Integer filmId;
    private Integer userId;
    private String userName;

    public UploadCommentVo(CommentUpload commentUpload){
        super(commentUpload);
    }
}
