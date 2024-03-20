package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.sys.entity.Comment;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
public interface ICommentService extends IService<Comment> {
    boolean saveComment(Comment comment);

    Map<String, Object> commentList(Integer filmId);

    Map<String, Object> selectCommentPage(String userName, Long pageNo, Long pageSize);

    boolean deleteComment(Integer commentId);
}
