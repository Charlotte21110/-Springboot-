package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.sys.entity.CommentAudit;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
public interface ICommentAuditService extends IService<CommentAudit> {
    Map<String,Object> selectAuditPage(String status, Long pageNo, Long pageSize);
}
