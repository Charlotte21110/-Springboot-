package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.ArticleAuditVo;
import com.design.filmr.common.vo.ArticleVo;
import com.design.filmr.common.vo.LikeQueryVo;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.ArticleAuditMapper;
import com.design.filmr.sys.mapper.ArticleMapper;
import com.design.filmr.sys.mapper.FilmMapper;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IArticleAuditService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xin.altitude.cms.common.util.EntityUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
@Service
public class ArticleAuditServiceImpl extends ServiceImpl<ArticleAuditMapper, ArticleAudit> implements IArticleAuditService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Map<String,Object> selectAuditPage(String status, Long pageNo, Long pageSize){
        Page<ArticleAudit> page = new Page<>(pageNo,pageSize);

        // 主表查询条件 status
        LambdaQueryWrapper<ArticleAudit> mainWrapper = Wrappers.lambdaQuery(ArticleAudit.class).eq(StringUtils.hasLength(status), ArticleAudit::getStatus, status);


        Page<ArticleAudit> auditPage = this.page(page,mainWrapper);
        IPage<ArticleAuditVo> articleVoIPage = EntityUtils.toPage(auditPage, ArticleAuditVo::new);

        // 获取连接查询的id，用于连接user表和article表
        Set<Integer> articleIds = EntityUtils.toSet(articleVoIPage.getRecords(), ArticleAudit::getArticleId);
        Set<Integer> userIds = EntityUtils.toSet(articleVoIPage.getRecords(), ArticleAudit::getUserId);

        if(userIds.size() > 0){
            // 副表查询条件 user表
            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> userList = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(userList, User::getUserId, e -> e);
            for (ArticleAuditVo audit : articleVoIPage.getRecords()) {
                User user = userMap.get(audit.getUserId());
                audit.setUserName(user.getUserName());
            }

        }
        // 副表查询条件 article
        if(articleIds.size() > 0){
            LambdaQueryWrapper<Article> articleWrapper = Wrappers.lambdaQuery(Article.class);
            articleWrapper.in(Article::getArticleId, articleIds);
            List<Article> articles = articleMapper.selectList(articleWrapper);
            Map<Integer, Article> articleMap = EntityUtils.toMap(articles, Article::getArticleId, e -> e);
            for (ArticleAuditVo audit : articleVoIPage.getRecords()) {
                Article article = articleMap.get(audit.getArticleId());
                audit.setFilmName(article.getFilmName());
                audit.setTime(article.getTime());
                audit.setTitle(article.getTitle());
                audit.setArticle(article.getArticle());
                audit.setDescription(article.getDescription());
            }

        }

        Comparator<ArticleAuditVo> comparator = Comparator.comparing(ArticleAuditVo::getAuditId);
        articleVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", articleVoIPage.getTotal());
        data.put("rows", articleVoIPage.getRecords());

        return data;
    }

    @Override
    public List<ArticleAuditVo> selectAuditConfirmList(){
        // 主表查询条件 status
        LambdaQueryWrapper<ArticleAudit> mainWrapper = Wrappers.lambdaQuery(ArticleAudit.class).eq(ArticleAudit::getStatus, "审核通过");


        List<ArticleAudit> articleAudits = this.baseMapper.selectList(mainWrapper);
        List<ArticleAuditVo> articleVos = EntityUtils.toList(articleAudits, ArticleAuditVo::new);

        // 获取连接查询的id，用于连接user表和article表
        Set<Integer> articleIds = EntityUtils.toSet(articleVos, ArticleAudit::getArticleId);
        Set<Integer> userIds = EntityUtils.toSet(articleVos, ArticleAudit::getUserId);

        if(userIds.size() > 0){
            // 副表查询条件 user表
            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> userList = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(userList, User::getUserId, e -> e);
            for (ArticleAuditVo audit : articleVos) {
                User user = userMap.get(audit.getUserId());
                audit.setUserName(user.getUserName());
            }

        }
        // 副表查询条件 article
        if(articleIds.size() > 0){
            LambdaQueryWrapper<Article> articleWrapper = Wrappers.lambdaQuery(Article.class);
            articleWrapper.in(Article::getArticleId, articleIds);
            List<Article> articles = articleMapper.selectList(articleWrapper);
            Map<Integer, Article> articleMap = EntityUtils.toMap(articles, Article::getArticleId, e -> e);
            for (ArticleAuditVo audit : articleVos) {
                Article article = articleMap.get(audit.getArticleId());
                audit.setFilmName(article.getFilmName());
                audit.setTime(article.getTime());
                audit.setTitle(article.getTitle());
                audit.setArticle(article.getArticle());
                audit.setDescription(article.getDescription());
            }

        }
        return articleVos;
    }
}
