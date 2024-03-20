package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
@TableName("comment_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "audit_id", type = IdType.AUTO)
    private Integer auditId;

    /**
     * 评论人id
     */
    private Integer userId;

    /**
     * 评论id
     */
    private Integer commentId;
    /**
     * 状态
     */
    private String status;
    /**
     * 审核信息
     */
    private String auditInfo;

    // 构造器 用于多表操作
    public CommentAudit(CommentAudit commentAudit) {
        if(Objects.nonNull(commentAudit)){
            this.commentId = commentAudit.commentId;
            this.status = commentAudit.status;
            this.userId = commentAudit.userId;
            this.auditInfo= commentAudit.auditInfo;
            this.auditId = commentAudit.auditId;
        }
    }

}
