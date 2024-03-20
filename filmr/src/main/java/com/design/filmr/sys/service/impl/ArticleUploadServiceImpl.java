package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.filmr.common.vo.ArticleAuditVo;
import com.design.filmr.common.vo.UploadArticleVo;
import com.design.filmr.common.vo.UploadCommentVo;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.ArticleAuditMapper;
import com.design.filmr.sys.mapper.ArticleMapper;
import com.design.filmr.sys.mapper.ArticleUploadMapper;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IArticleUploadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xin.altitude.cms.common.util.EntityUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-22
 */
@Service
public class ArticleUploadServiceImpl extends ServiceImpl<ArticleUploadMapper, ArticleUpload> implements IArticleUploadService {

    @Resource
    private ArticleAuditMapper articleAuditMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String,Object> selectConfirmArticlePage(String uploadStatus, Long pageNo, Long pageSize){
        Page<ArticleUpload> page = new Page<>(pageNo,pageSize);

        // 主表查询条件 uploadStatus
        LambdaQueryWrapper<ArticleUpload> mainWrapper = Wrappers.lambdaQuery(ArticleUpload.class).eq(StringUtils.hasLength(uploadStatus), ArticleUpload::getUploadStatus, uploadStatus);

        Page<ArticleUpload> uploadPage = this.page(page,mainWrapper);
        IPage<UploadArticleVo> uploadArticleVoIPage = EntityUtils.toPage(uploadPage, UploadArticleVo::new);

        // 获取连接查询的id，用于连接article_audit
        Set<Integer> auditIds = EntityUtils.toSet(uploadArticleVoIPage.getRecords(), UploadArticleVo::getAuditId);

        if(auditIds.size() > 0){
            // 副表查询条件 article_audit表
            LambdaQueryWrapper<ArticleAudit> auditWrapper = Wrappers.lambdaQuery(ArticleAudit.class);
            auditWrapper.in(ArticleAudit::getAuditId, auditIds);
            List<ArticleAudit> articleAuditList = articleAuditMapper.selectList(auditWrapper);
            Map<Integer, ArticleAudit> auditMap = EntityUtils.toMap(articleAuditList, ArticleAudit::getAuditId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadArticleVo uploadVo : uploadArticleVoIPage.getRecords()) {
                ArticleAudit articleAudit = auditMap.get(uploadVo.getAuditId());
                uploadVo.setAuditInfo(articleAudit.getAuditInfo());
                uploadVo.setUserId(articleAudit.getUserId());
                uploadVo.setStatus(articleAudit.getStatus());
                uploadVo.setArticleId(articleAudit.getArticleId());
            }

            // 通过article_audit再关联Article表和user表
            Set<Integer> articleIds = EntityUtils.toSet(uploadArticleVoIPage.getRecords(), UploadArticleVo::getArticleId);

            Set<Integer> userIds = uploadArticleVoIPage.getRecords().stream()
                    .map(UploadArticleVo::getUserId).collect(Collectors.toSet());



//            Set<Integer> userIds = EntityUtils.toSet(uploadArticleVoIPage.getRecords(), UploadArticleVo::getUserId);

            // 理论上上面判断语句进来后，程序正常运行一定有相应的article_audit表，换句话说一定有articleIds
            LambdaQueryWrapper<Article> articleWrapper = Wrappers.lambdaQuery(Article.class);
            articleWrapper.in(Article::getArticleId, articleIds);
            List<Article> articleList = articleMapper.selectList(articleWrapper);
            Map<Integer, Article> articleMap = EntityUtils.toMap(articleList, Article::getArticleId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadArticleVo uploadVo : uploadArticleVoIPage.getRecords()) {
                Article article = articleMap.get(uploadVo.getArticleId());
                uploadVo.setArticle(article.getArticle());
                uploadVo.setDescription(article.getDescription());
//                uploadVo.setUserId(article.getUserId());
                uploadVo.setFilmName(article.getFilmName());
                uploadVo.setTitle(article.getTitle());
                uploadVo.setTime(article.getTime());
            }



            // 拼装userName
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(User::getUserId, userIds);
            List<User> users = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(users, User::getUserId, e -> e);
            for (UploadArticleVo uploadVo : uploadArticleVoIPage.getRecords()){
//                System.out.println(uploadVo.getUserId()+"--");
                User user = userMap.get(uploadVo.getUserId());

                uploadVo.setUserName(user.getUserName());
            }
        }

        Comparator<UploadArticleVo> comparator = Comparator.comparing(UploadArticleVo::getUploadId);
        uploadArticleVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", uploadArticleVoIPage.getTotal());
        data.put("rows", uploadArticleVoIPage.getRecords());
        return data;
    }

    @Override
    public List<UploadArticleVo> selectConfirmList(){
        // 主表查询条件 uploadStatus
        LambdaQueryWrapper<ArticleUpload> mainWrapper = Wrappers.lambdaQuery(ArticleUpload.class).eq       (ArticleUpload::getUploadStatus, "已上架");

        List<ArticleUpload> articleUploads = this.baseMapper.selectList(mainWrapper);
        List<UploadArticleVo> uploadArticleVos = EntityUtils.toList(articleUploads, UploadArticleVo::new);

        // 获取连接查询的id，用于连接article_audit
        Set<Integer> auditIds = EntityUtils.toSet(uploadArticleVos, UploadArticleVo::getAuditId);

        if(auditIds.size() > 0){
            // 副表查询条件 article_audit表
            LambdaQueryWrapper<ArticleAudit> auditWrapper = Wrappers.lambdaQuery(ArticleAudit.class);
            auditWrapper.in(ArticleAudit::getAuditId, auditIds);
            List<ArticleAudit> articleAuditList = articleAuditMapper.selectList(auditWrapper);
            Map<Integer, ArticleAudit> auditMap = EntityUtils.toMap(articleAuditList, ArticleAudit::getAuditId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadArticleVo uploadVo : uploadArticleVos) {
                ArticleAudit articleAudit = auditMap.get(uploadVo.getAuditId());
                uploadVo.setAuditInfo(articleAudit.getAuditInfo());
                uploadVo.setUserId(articleAudit.getUserId());
                uploadVo.setStatus(articleAudit.getStatus());
                uploadVo.setArticleId(articleAudit.getArticleId());
            }

            // 通过article_audit再关联Article表和user表
            Set<Integer> articleIds = EntityUtils.toSet(uploadArticleVos, UploadArticleVo::getArticleId);
            Set<Integer> userIds = EntityUtils.toSet(uploadArticleVos, UploadArticleVo::getUserId);

            // 理论上上面判断语句进来后，程序正常运行一定有相应的film_audit表，换句话说一定有commentIds
            LambdaQueryWrapper<Article> articleWrapper = Wrappers.lambdaQuery(Article.class);
            articleWrapper.in(Article::getArticleId, articleIds);
            List<Article> articleList = articleMapper.selectList(articleWrapper);
            Map<Integer, Article> articleMap = EntityUtils.toMap(articleList, Article::getArticleId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadArticleVo uploadVo : uploadArticleVos) {
                Article article = articleMap.get(uploadVo.getArticleId());
                uploadVo.setArticle(article.getArticle());
                uploadVo.setDescription(article.getDescription());
                uploadVo.setFilmName(article.getFilmName());
                uploadVo.setTitle(article.getTitle());
                uploadVo.setTime(article.getTime());

            }

            // 拼装userName
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.in(User::getUserId, userIds);
            List<User> users = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(users, User::getUserId, e -> e);
            for (UploadArticleVo uploadVo : uploadArticleVos){
                User user = userMap.get(uploadVo.getUserId());
                uploadVo.setUserName(user.getUserName());
            }
        }
        return uploadArticleVos;
    }

}
