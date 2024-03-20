package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.ArticleVo;
import com.design.filmr.common.vo.CommentAuditVo;
import com.design.filmr.common.vo.CommentVo;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.CommentAuditMapper;
import com.design.filmr.sys.mapper.CommentMapper;
import com.design.filmr.sys.mapper.CommentUploadMapper;
import com.design.filmr.sys.mapper.FilmMapper;
import com.design.filmr.sys.service.ICommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Resource
    private FilmMapper filmMapper;
    @Resource
    private CommentAuditMapper commentAuditMapper;
    @Resource
    private CommentUploadMapper commentUploadMapper;

    @Override
    public boolean saveComment(Comment comment){
        boolean flag1 = false;
        boolean flag2 = false;
        // 评论保存
        flag1 = this.baseMapper.insert(comment) > 0;
        // 审核表
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        // 评论、评论者和评论电影id标识一个comment
        wrapper.eq(StringUtils.hasLength(comment.getComment()), Comment::getComment, comment.getComment());
        wrapper.eq(comment.getUserId() != null, Comment::getUserId, comment.getUserId());
        wrapper.eq(comment.getFilmId() != null, Comment::getFilmId, comment.getFilmId());
        Comment targetComment = this.baseMapper.selectOne(wrapper);

        CommentAudit commentAudit = new CommentAudit();
        commentAudit.setStatus("待审核");
        commentAudit.setCommentId(targetComment.getCommentId());
        commentAudit.setUserId(targetComment.getUserId());

        flag2 = commentAuditMapper.insert(commentAudit)> 0;

        return flag1 && flag2;
    }

    @Override
    public Map<String, Object> commentList(Integer filmId){
        // 获取所有评论信息
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getFilmId, filmId);
        List<Comment> comments = this.baseMapper.selectList(wrapper);
        Set<Integer> commentIds = EntityUtils.toSet(comments, Comment::getCommentId);

        if(commentIds.size() > 0){
            Map<Integer, Comment> commentMap = EntityUtils.toMap(comments, Comment::getCommentId, e -> e);
            // 查询审核通过的评论信息
            LambdaQueryWrapper<CommentAudit> auditWrapper = new LambdaQueryWrapper<>();
            auditWrapper.in(CommentAudit::getCommentId, commentIds);
            auditWrapper.eq(CommentAudit::getStatus, "审核通过");
            List<CommentAudit> commentAudits = commentAuditMapper.selectList(auditWrapper);
            List<CommentAuditVo> commentAuditVos = EntityUtils.toList(commentAudits, CommentAuditVo::new);

            // 数据组装
            for (CommentAuditVo commentVo : commentAuditVos) {
                Comment comment = commentMap.get(commentVo.getCommentId());
                commentVo.setUserName(comment.getUserName());
                commentVo.setComment(comment.getComment());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("comments", commentAuditVos);
            return map;
        }

        return null;
    }

    @Override
    public Map<String, Object> selectCommentPage(String userName, Long pageNo, Long pageSize){
        Page<Comment> page = new Page<>(pageNo, pageSize);
        // 主表comment
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasLength(userName), Comment::getUserName, userName);
        Page<Comment> commentPage = this.page(page, wrapper);
        IPage<CommentVo> commentVoIPage = EntityUtils.toPage(commentPage, CommentVo::new);

        // 获取连接film表的ids
        Set<Integer> filmIds = EntityUtils.toSet(commentVoIPage.getRecords(), CommentVo::getFilmId);
        Set<Integer> commentIds = EntityUtils.toSet(commentVoIPage.getRecords(), CommentVo::getCommentId);

        // film
        if(filmIds.size() > 0){
            LambdaQueryWrapper<Film> filmWrapper = Wrappers.lambdaQuery(Film.class);
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> films = filmMapper.selectList(filmWrapper);
            Map<Integer, Film> filmMap = EntityUtils.toMap(films, Film::getFilmId, e -> e);
            // 数据组装
            for (CommentVo commentVo : commentVoIPage.getRecords()) {
                Film film = filmMap.get(commentVo.getFilmId());
                commentVo.setFilmName(film.getFilmName());
            }
        }

        // comment_audit
        if(commentIds.size() > 0){
            LambdaQueryWrapper<CommentAudit> auditWrapper = Wrappers.lambdaQuery(CommentAudit.class);
            auditWrapper.in(CommentAudit::getCommentId, commentIds);
            List<CommentAudit> audits = commentAuditMapper.selectList(auditWrapper);
            Map<Integer, CommentAudit> auditMap = EntityUtils.toMap(audits, CommentAudit::getCommentId, e -> e);
            // 数据组装
            for (CommentVo commentVo : commentVoIPage.getRecords()) {
                CommentAudit audit = auditMap.get(commentVo.getCommentId());
                commentVo.setStatus(audit.getStatus());
            }
        }

        Comparator<CommentVo> comparator = Comparator.comparing(CommentVo::getCommentId);
        commentVoIPage.getRecords().sort(comparator.reversed());


        Map<String, Object> data = new HashMap<>();
        data.put("total", commentVoIPage.getTotal());
        data.put("rows", commentVoIPage.getRecords());
        return data;
    }

    @Override
    public boolean deleteComment(Integer commentId){
        boolean flag1 = false;
        boolean flag2 = false;
        // comment表数据删除
        flag1 = this.baseMapper.deleteById(commentId) > 0;
        LambdaQueryWrapper<CommentAudit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(commentId != null, CommentAudit::getCommentId, commentId);
        CommentAudit commentAudit = commentAuditMapper.selectOne(wrapper);
        flag2 = commentAuditMapper.delete(wrapper) > 0;
        // 删除comment_upload表信息
        if(commentAudit.getStatus().equals("审核通过")){
            LambdaQueryWrapper<CommentUpload> uploadWrapper = new LambdaQueryWrapper<>();
            uploadWrapper.eq(CommentUpload::getAuditId, commentAudit.getAuditId());

            flag2 = flag2 && (commentUploadMapper.delete(uploadWrapper) > 0);
        }
        return flag1 && flag2;
    }
}
