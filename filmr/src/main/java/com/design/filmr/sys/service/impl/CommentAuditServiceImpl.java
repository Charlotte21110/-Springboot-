package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.ArticleAuditVo;
import com.design.filmr.common.vo.CommentAuditVo;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.CommentAuditMapper;
import com.design.filmr.sys.mapper.CommentMapper;
import com.design.filmr.sys.service.ICommentAuditService;
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
public class CommentAuditServiceImpl extends ServiceImpl<CommentAuditMapper, CommentAudit> implements ICommentAuditService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public Map<String,Object> selectAuditPage(String status, Long pageNo, Long pageSize){
        Page<CommentAudit> page = new Page<>(pageNo,pageSize);

        // 主表查询条件 status
        LambdaQueryWrapper<CommentAudit> mainWrapper = Wrappers.lambdaQuery(CommentAudit.class).eq(StringUtils.hasLength(status), CommentAudit::getStatus, status);


        Page<CommentAudit> auditPage = this.page(page,mainWrapper);
        IPage<CommentAuditVo> commentAuditVoIPage = EntityUtils.toPage(auditPage, CommentAuditVo::new);

        // 获取连接查询的id，用于连接comment表
        Set<Integer> commentIds = EntityUtils.toSet(commentAuditVoIPage.getRecords(), CommentAudit::getCommentId);

        // 副表 comment
        if(commentIds.size() > 0){
            LambdaQueryWrapper<Comment> commentWrapper = Wrappers.lambdaQuery(Comment.class);
            commentWrapper.in(Comment::getCommentId, commentIds);
            List<Comment> comments = commentMapper.selectList(commentWrapper);
            Map<Integer, Comment> commentMap = EntityUtils.toMap(comments, Comment::getCommentId, e -> e);
            for (CommentAuditVo audit : commentAuditVoIPage.getRecords()) {
                Comment comment = commentMap.get(audit.getCommentId());
                audit.setUserName(comment.getUserName());
                audit.setComment(comment.getComment());
            }

        }
        Comparator<CommentAuditVo> comparator = Comparator.comparing(CommentAuditVo::getAuditId);
        commentAuditVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", commentAuditVoIPage.getTotal());
        data.put("rows", commentAuditVoIPage.getRecords());

        return data;
    }
}
