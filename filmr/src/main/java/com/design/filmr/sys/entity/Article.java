package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "article_id", type = IdType.AUTO)
    private Integer articleId;

    private String title;

    private Integer userId;

    private String filmName;

    private String article;

    private String time;

    private String description;

    // 构造器 用于多表操作
    public Article(Article article) {
        if(Objects.nonNull(article)){
            this.articleId = article.articleId;
            this.filmName = article.filmName;
            this.userId = article.userId;
            this.title = article.title;
            this.article = article.article;
            this.time = article.time;
            this.description = article.description;
        }
    }

}
