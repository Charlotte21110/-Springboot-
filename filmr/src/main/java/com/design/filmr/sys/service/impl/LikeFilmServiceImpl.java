package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.LikeQueryVo;
import com.design.filmr.sys.entity.Film;
import com.design.filmr.sys.entity.LikeFilm;
import com.design.filmr.sys.mapper.FilmMapper;
import com.design.filmr.sys.mapper.LikeFilmMapper;
import com.design.filmr.sys.service.IFilmService;
import com.design.filmr.sys.service.ILikeFilmService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LikeFilmServiceImpl extends ServiceImpl<LikeFilmMapper, LikeFilm> implements ILikeFilmService {

    @Resource
    private FilmMapper filmMapper;

    @Override
    public Map<String, Object> selectLikePage(Integer isPopular, Long pageNo, Long pageSize) {
        Page<LikeFilm> page = new Page<>(pageNo,pageSize);

        // 主表 点赞表
        LambdaQueryWrapper<LikeFilm> wrapper = Wrappers.lambdaQuery(LikeFilm.class);
        wrapper.eq(isPopular != null, LikeFilm::getIsPopular, isPopular);
        Page<LikeFilm> filmPage = this.page(page, wrapper);
        IPage<LikeQueryVo> likeQueryVoIPage = EntityUtils.toPage(filmPage, LikeQueryVo::new);

        // 获取连接查询的id，用于连接like_film表
        Set<Integer> filmIds = EntityUtils.toSet(likeQueryVoIPage.getRecords(), LikeQueryVo::getFilmId);

        // 副表 电影表
        if(filmIds.size() > 0){
            LambdaQueryWrapper<Film> filmWrapper = Wrappers.lambdaQuery(Film.class);
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> films = filmMapper.selectList(filmWrapper);
            Map<Integer, Film> filmMap = EntityUtils.toMap(films, Film::getFilmId, e -> e);
            // 数据组装
            for (LikeQueryVo likeVo : likeQueryVoIPage.getRecords()) {
                Film film = filmMap.get(likeVo.getFilmId());
                likeVo.setImgUrl(film.getImgUrl());
                likeVo.setFilmName(film.getFilmName());
            }
        }

        // 根据点赞数据倒序输出
        Comparator<LikeQueryVo> likeComparator = Comparator.comparing(LikeQueryVo::getCount);
        likeQueryVoIPage.getRecords().sort(likeComparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", likeQueryVoIPage.getTotal());
        data.put("rows", likeQueryVoIPage.getRecords());
        return data;
    }

    @Override
    public List<Film> selectPopFilm(){
        LambdaQueryWrapper<LikeFilm> wrapper = new LambdaQueryWrapper<>();
        // 1:热门
        wrapper.eq(LikeFilm::getIsPopular, 1);
        List<LikeFilm> likeFilms = this.baseMapper.selectList(wrapper);

        Set<Integer> filmIds = EntityUtils.toSet(likeFilms, LikeFilm::getFilmId);
        if(filmIds.size() > 0){
            LambdaQueryWrapper<Film> filmWrapper = new LambdaQueryWrapper<>();
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> films = filmMapper.selectList(filmWrapper);
            if(!films.isEmpty()){
                return films;
            }
        }
        return null;
    }

    /**
     * 更新点赞数据
     *
     * @param filmId
     * @return
     */
    @Override
    public boolean updateLikeData(int filmId) {
        LambdaQueryWrapper<LikeFilm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeFilm::getFilmId, filmId);
        LikeFilm likeFilm = this.baseMapper.selectOne(wrapper);

        if(likeFilm != null){
            likeFilm.setCount(likeFilm.getCount() + 1);
            return updateById(likeFilm);
        }else {
            return false;
        }
    }

    /**
     * 更新热门标志
     * @param filmId
     * @param isPopular
     * @return
     */
    @Override
    public boolean makePop(int filmId, int isPopular) {
        LambdaQueryWrapper<LikeFilm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeFilm::getFilmId, filmId);
        LikeFilm likeFilm = this.baseMapper.selectOne(wrapper);

        likeFilm.setIsPopular(isPopular);
        return updateById(likeFilm);
    }


}
