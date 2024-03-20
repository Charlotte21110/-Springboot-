package com.design.filmr.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.design.filmr.sys.entity.FilmAudit;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Gzee
 * @since 2023-10-04
 */
public interface FilmAuditMapper extends BaseMapper<FilmAudit> {
    Integer getAuditIdByFilmId(Integer filmId);
}
