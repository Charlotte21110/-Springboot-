package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.common.vo.FilmQueryVo;
import com.design.filmr.sys.entity.Film;

import java.util.Map;

public interface IFilmService extends IService<Film> {

    boolean saveFilm(FilmQueryVo filmQueryVo);

    boolean deleteFilm(Integer filmId);

    Map<String,Object> selectFilmPage(String filmName, String filmArea, String filmCategory, Long pageNo, Long pageSize);

    boolean updateFilm(FilmQueryVo filmQueryVo);

}
