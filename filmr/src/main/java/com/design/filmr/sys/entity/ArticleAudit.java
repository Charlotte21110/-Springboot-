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
@TableName("article_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "audit_id", type = IdType.AUTO)
    private Integer auditId;

    private Integer userId;

    private Integer articleId;

    private String status;

    /**
     * 审核信息
     */
    private String auditInfo;

    // 构造器 用于多表操作
    public ArticleAudit(ArticleAudit articleAudit) {
        if(Objects.nonNull(articleAudit)){
            this.articleId = articleAudit.articleId;
            this.auditId = articleAudit.auditId;
            this.userId = articleAudit.userId;
            this.status = articleAudit.status;
            this.auditInfo = articleAudit.auditInfo;
        }
    }

}
