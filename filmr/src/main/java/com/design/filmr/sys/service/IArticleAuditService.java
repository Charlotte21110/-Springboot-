package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.common.vo.ArticleAuditVo;
import com.design.filmr.sys.entity.ArticleAudit;

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
public interface IArticleAuditService extends IService<ArticleAudit> {
    Map<String,Object> selectAuditPage(String status, Long pageNo, Long pageSize);

    List<ArticleAuditVo> selectAuditConfirmList();
}
