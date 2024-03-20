package com.design.filmr.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.design.filmr.sys.entity.ArticleAudit;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
public interface ArticleAuditMapper extends BaseMapper<ArticleAudit> {
    Integer getAuditIdByArticleId(Integer articleId);
}
