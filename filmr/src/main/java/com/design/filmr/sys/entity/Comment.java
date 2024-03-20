package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    /**
     * 评论
     */
    private String comment;

    /**
     * 评论者
     */
    private String userName;

    /**
     * 评论者id
     */
    private Integer userId;

    /**
     * 评论电影id
     */
    private Integer filmId;

    // 构造器 用于多表操作
    public Comment(Comment comment) {
        if(Objects.nonNull(comment)){
            this.commentId = comment.commentId;
            this.userName = comment.userName;
            this.userId = comment.userId;
            this.filmId = comment.filmId;
            this.comment = comment.comment;
        }
    }

}
