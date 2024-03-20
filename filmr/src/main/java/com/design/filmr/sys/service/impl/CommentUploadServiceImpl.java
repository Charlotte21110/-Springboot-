package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.filmr.common.vo.CommentVo;
import com.design.filmr.common.vo.UploadCommentVo;
import com.design.filmr.common.vo.UploadFilmVo;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.CommentAuditMapper;
import com.design.filmr.sys.mapper.CommentMapper;
import com.design.filmr.sys.mapper.CommentUploadMapper;
import com.design.filmr.sys.service.ICommentUploadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * @since 2023-10-22
 */
@Service
public class CommentUploadServiceImpl extends ServiceImpl<CommentUploadMapper, CommentUpload> implements ICommentUploadService {

    @Resource
    private CommentAuditMapper commentAuditMapper;
    @Resource
    private CommentMapper commentMapper;

    @Override
    public Map<String,Object> selectConfirmCommentPage(String uploadStatus, Long pageNo, Long pageSize){
        Page<CommentUpload> page = new Page<>(pageNo,pageSize);

        // 主表查询条件 uploadStatus
        LambdaQueryWrapper<CommentUpload> mainWrapper = Wrappers.lambdaQuery(CommentUpload.class).eq(StringUtils.hasLength(uploadStatus), CommentUpload::getUploadStatus, uploadStatus);

        Page<CommentUpload> uploadPage = this.page(page,mainWrapper);
        IPage<UploadCommentVo> uploadCommentVoIPage = EntityUtils.toPage(uploadPage, UploadCommentVo::new);

        // 获取连接查询的id，用于连接comment_audit
        Set<Integer> auditIds = EntityUtils.toSet(uploadCommentVoIPage.getRecords(), UploadCommentVo::getAuditId);

        if(auditIds.size() > 0){
            // 副表查询条件 comment_audit表
            LambdaQueryWrapper<CommentAudit> auditWrapper = Wrappers.lambdaQuery(CommentAudit.class);
            auditWrapper.in(CommentAudit::getAuditId, auditIds);
            List<CommentAudit> commentAuditList = commentAuditMapper.selectList(auditWrapper);
            Map<Integer, CommentAudit> auditMap = EntityUtils.toMap(commentAuditList, CommentAudit::getAuditId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadCommentVo uploadVo : uploadCommentVoIPage.getRecords()) {
                CommentAudit commentAudit = auditMap.get(uploadVo.getAuditId());
                uploadVo.setAuditInfo(commentAudit.getAuditInfo());
                uploadVo.setStatus(commentAudit.getStatus());
                uploadVo.setCommentId(commentAudit.getCommentId());
            }

            // 通过comment_audit再关联comment表
            Set<Integer> commentIds = EntityUtils.toSet(uploadCommentVoIPage.getRecords(), UploadCommentVo::getCommentId);

            // 理论上上面判断语句进来后，程序正常运行一定有相应的film_audit表，换句话说一定有commentIds
            LambdaQueryWrapper<Comment> commentWrapper = Wrappers.lambdaQuery(Comment.class);
            commentWrapper.in(Comment::getCommentId, commentIds);
            List<Comment> commentList = commentMapper.selectList(commentWrapper);
            Map<Integer, Comment> commentMap = EntityUtils.toMap(commentList, Comment::getCommentId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadCommentVo uploadVo : uploadCommentVoIPage.getRecords()) {
                Comment comment = commentMap.get(uploadVo.getCommentId());
                uploadVo.setComment(comment.getComment());
                uploadVo.setFilmId(comment.getFilmId());
                uploadVo.setUserId(comment.getUserId());
                uploadVo.setUserName(comment.getUserName());
            }
        }

        Comparator<UploadCommentVo> comparator = Comparator.comparing(UploadCommentVo::getUploadId);
        uploadCommentVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", uploadCommentVoIPage.getTotal());
        data.put("rows", uploadCommentVoIPage.getRecords());
        return data;
    }

    @Override
    public List<UploadCommentVo> selectConfirmList() {
        LambdaQueryWrapper<CommentUpload> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(CommentUpload::getUploadStatus, "已上架");
        List<CommentUpload> commentUploads = this.baseMapper.selectList(mainWrapper);

        // 获取连接查询的id，用于连接comment_audit
        List<UploadCommentVo> uploadCommentVos = EntityUtils.toList(commentUploads, UploadCommentVo::new);
        Set<Integer> auditIds = EntityUtils.toSet(uploadCommentVos, CommentUpload::getAuditId);

        if (auditIds.size() > 0) {
            // 副表查询条件 film_audit表
            LambdaQueryWrapper<CommentAudit> auditWrapper = Wrappers.lambdaQuery(CommentAudit.class);
            auditWrapper.in(CommentAudit::getAuditId, auditIds);
            List<CommentAudit> commentAuditList = commentAuditMapper.selectList(auditWrapper);
            Map<Integer, CommentAudit> auditMap = EntityUtils.toMap(commentAuditList, CommentAudit::getAuditId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadCommentVo uploadVo : uploadCommentVos) {
                CommentAudit commentAudit = auditMap.get(uploadVo.getAuditId());
                uploadVo.setAuditInfo(commentAudit.getAuditInfo());
                uploadVo.setStatus(commentAudit.getStatus());
                uploadVo.setCommentId(commentAudit.getCommentId());
            }

            // 通过comment_audit再关联comment表
            Set<Integer> commentIds = EntityUtils.toSet(uploadCommentVos, UploadCommentVo::getCommentId);

            // 理论上上面判断语句进来后，程序正常运行一定有相应的film_audit表，换句话说一定有commentIds
            LambdaQueryWrapper<Comment> commentWrapper = Wrappers.lambdaQuery(Comment.class);
            commentWrapper.in(Comment::getCommentId, commentIds);
            List<Comment> commentList = commentMapper.selectList(commentWrapper);
            Map<Integer, Comment> commentMap = EntityUtils.toMap(commentList, Comment::getCommentId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadCommentVo uploadVo : uploadCommentVos) {
                Comment comment = commentMap.get(uploadVo.getCommentId());
                uploadVo.setComment(comment.getComment());
                uploadVo.setFilmId(comment.getFilmId());
                uploadVo.setUserId(comment.getUserId());
                uploadVo.setUserName(comment.getUserName());
            }
        }

        return uploadCommentVos;
    }
}
