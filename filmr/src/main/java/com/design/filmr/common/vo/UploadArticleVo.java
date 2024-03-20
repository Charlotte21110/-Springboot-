package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.ArticleUpload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadArticleVo extends ArticleUpload {
    // article_audit表信息
    private Integer articleId;
    private String status;
    private String auditInfo;

    // article表信息
    private String title;
    private Integer userId;
    private String filmName;
    private String article;
    private String time;
    private String description;

    // user表信息
    private String userName;

    public UploadArticleVo(ArticleUpload articleUpload){
        super(articleUpload);
    }
}
