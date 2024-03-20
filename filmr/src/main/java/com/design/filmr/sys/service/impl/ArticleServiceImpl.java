package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.ArticleAuditVo;
import com.design.filmr.common.vo.ArticleVo;
import com.design.filmr.common.vo.FilmQueryVo;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.ArticleAuditMapper;
import com.design.filmr.sys.mapper.ArticleMapper;
import com.design.filmr.sys.mapper.ArticleUploadMapper;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IArticleAuditService;
import com.design.filmr.sys.service.IArticleService;
import io.swagger.models.auth.In;
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
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private ArticleAuditMapper articleAuditMapper;
    @Resource
    private ArticleUploadMapper articleUploadMapper;

    @Override
    public boolean saveArticle(Article article, Integer userId) {
        boolean flag1 = false;
        boolean flag2 = false;

        ArticleAudit articleAudit = new ArticleAudit();

        // article表添加
        flag1 = this.baseMapper.insert(article) > 0;

        // article_audit 添加 title标识article
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(article.getTitle()), Article::getTitle, article.getTitle());
        Article target = this.baseMapper.selectOne(wrapper);

        articleAudit.setArticleId(target.getArticleId());
        articleAudit.setUserId(userId);
        articleAudit.setStatus("待审核");

        flag2 = articleAuditMapper.insert(articleAudit) > 0;

        return flag1 && flag2;
    }

    /**
     * 删除article表数据，同时删除对应审核表数据
     * @param articleId
     * @return
     */
    public boolean deleteArticle(Integer articleId){
        boolean flag1 = false;
        boolean flag2 = false;
        // 删除article数据
        flag1 = this.baseMapper.deleteById(articleId) > 0;
        // 删除审核表数据
        LambdaQueryWrapper<ArticleAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleAudit::getArticleId, articleId);
        ArticleAudit articleAudit = articleAuditMapper.selectOne(wrapper);
        flag2 = articleAuditMapper.deleteById(articleAuditMapper.getAuditIdByArticleId(articleId)) > 0;

        LambdaQueryWrapper<ArticleUpload> uploadWrapper = new LambdaQueryWrapper<>();
        uploadWrapper.eq(ArticleUpload::getAuditId, articleAudit.getAuditId());
        ArticleUpload articleUpload = articleUploadMapper.selectOne(uploadWrapper);
        if(articleUpload != null){
            articleUploadMapper.delete(uploadWrapper);
        }

        return flag1 && flag2;
    }

    @Override
    public Map<String, Object> selectArticlePage(String title, String filmName, Long pageNo, Long pageSize) {
        Page<Article> page = new Page<>(pageNo,pageSize);

        // 主表为Article表
        LambdaQueryWrapper<Article> wrapper = Wrappers.lambdaQuery(Article.class);
        // 主表条件 传来的filmName和title查询
        wrapper.eq(StringUtils.hasLength(filmName), Article::getFilmName, filmName);
        wrapper.eq(StringUtils.hasLength(title), Article::getTitle, title);
        Page<Article> articlePage = this.page(page, wrapper);
        IPage<ArticleVo> articleVoIPage = EntityUtils.toPage(articlePage, ArticleVo::new);

        // 获取连接查询的id，用于连接user表
        Set<Integer> userIds = EntityUtils.toSet(articleVoIPage.getRecords(), ArticleVo::getUserId);

        // 副表 user
        if(userIds.size() > 0){
            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> users = userMapper.selectList(userWrapper);
            Map<Integer, User> filmMap = EntityUtils.toMap(users, User::getUserId, e -> e);
            // 数据组装
            for (ArticleVo articleVo : articleVoIPage.getRecords()) {
                User user = filmMap.get(articleVo.getUserId());
                articleVo.setUserName(user.getUserName());
            }
        }

        Comparator<ArticleVo> comparator = Comparator.comparing(ArticleVo::getArticleId);
        articleVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", articleVoIPage.getTotal());
        data.put("rows", articleVoIPage.getRecords());
        return data;
    }

    @Override
    public boolean updateArticle(Article article){
        boolean flag1 = false;
        boolean flag2 = false;

        flag1 = this.baseMapper.updateById(article) > 0;


        // 审核表修改
        LambdaQueryWrapper<ArticleAudit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleAudit::getArticleId, article.getArticleId());
        ArticleAudit articleAudit = articleAuditMapper.selectOne(queryWrapper);

        // 更新后重置审核信息，需要重新审核
        articleAudit.setAuditInfo("");
        articleAudit.setStatus("待审核");
        flag2 = articleAuditMapper.updateById(articleAudit) > 0;

        LambdaQueryWrapper<ArticleUpload> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleUpload::getAuditId, articleAudit.getAuditId());
        ArticleUpload articleUpload = articleUploadMapper.selectOne(wrapper);
        if(articleUpload != null){
            articleUploadMapper.delete(wrapper);
        }

        return flag1 && flag2;
    }

    @Override
    public List<ArticleVo> selectArticleList(){
        LambdaQueryWrapper<ArticleAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleAudit::getStatus, "审核通过");

        List<ArticleAudit> articleAudits = articleAuditMapper.selectList(wrapper);
        // 获取连接查询的id，用于连接user表
        Set<Integer> articleIds = EntityUtils.toSet(articleAudits, ArticleAudit::getArticleId);

        if(articleIds.size() <= 0){
            return null;
        }

        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.in(Article::getArticleId, articleIds);
        // Article转ArticleVo
        List<ArticleVo> articleVos = EntityUtils.toList(this.baseMapper.selectList(articleWrapper), ArticleVo::new);

        // 获取连接查询的id，用于连接user表
        Set<Integer> userIds = EntityUtils.toSet(articleVos, ArticleVo::getUserId);

        // 副表 user
        if(userIds.size() > 0){
            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> users = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(users, User::getUserId, e -> e);
            // 数据组装
            for (ArticleVo articleVo : articleVos) {
                User user = userMap.get(articleVo.getUserId());
                articleVo.setUserName(user.getUserName());
            }
            return articleVos;
        }

        return null;
    }
}
