package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.sys.entity.Film;
import com.design.filmr.sys.entity.LikeFilm;

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
public interface ILikeFilmService extends IService<LikeFilm> {
    Map<String, Object> selectLikePage(Integer isPopular, Long pageNo, Long pageSize);

    boolean updateLikeData(int filmId);

    boolean makePop(int filmId, int isPopular);

    List<Film> selectPopFilm();
}
