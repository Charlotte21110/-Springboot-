package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVo extends Article {
    // 用户表
    private String userName;

    public ArticleVo(Article article){
        super(article);
    }
}
