package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.ArticleAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAuditVo extends ArticleAudit {
    // user表信息
    private String userName;
    // article表信息
    private String title;

    private String filmName;

    private String article;

    private String time;

    private String description;

    public ArticleAuditVo(ArticleAudit articleAudit){
        super(articleAudit);
    }
}
