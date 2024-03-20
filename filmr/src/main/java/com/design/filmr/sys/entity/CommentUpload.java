package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Gzee
 * @since 2023-10-22
 */
@TableName("comment_upload")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 上架id
     */
    @TableId(value = "upload_id", type = IdType.AUTO)
    private Integer uploadId;

    /**
     * 审核评论id
     */
    private Integer auditId;

    /**
     * 上架状态
     */
    private String uploadStatus;

    public CommentUpload(CommentUpload commentUpload) {
        this.auditId = commentUpload.auditId;
        this.uploadId = commentUpload.uploadId;
        this.uploadStatus = commentUpload.uploadStatus;
    }
}
