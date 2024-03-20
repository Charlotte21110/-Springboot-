package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.common.vo.ArticleVo;
import com.design.filmr.sys.entity.Article;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
public interface IArticleService extends IService<Article> {
    Map<String, Object> selectArticlePage(String title, String filmName, Long pageNo, Long pageSize);

    boolean saveArticle(Article article, Integer userId);

    boolean deleteArticle(Integer articleId);

    boolean updateArticle(Article article);

    List<ArticleVo> selectArticleList();
}
