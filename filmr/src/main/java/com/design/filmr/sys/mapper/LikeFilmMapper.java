package com.design.filmr.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.design.filmr.sys.entity.LikeFilm;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
public interface LikeFilmMapper extends BaseMapper<LikeFilm> {
    Integer getLikeIdByFilmId(Integer filmId);
}
