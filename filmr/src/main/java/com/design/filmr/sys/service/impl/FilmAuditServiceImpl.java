package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.common.vo.CommentAuditVo;
import com.design.filmr.sys.entity.Film;
import com.design.filmr.sys.entity.FilmAudit;
import com.design.filmr.sys.entity.User;
import com.design.filmr.sys.mapper.FilmAuditMapper;
import com.design.filmr.sys.mapper.FilmMapper;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IFilmAuditService;
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
 * @since 2023-10-04
 */
@Service
public class FilmAuditServiceImpl extends ServiceImpl<FilmAuditMapper, FilmAudit> implements IFilmAuditService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private FilmMapper filmMapper;

    /**
     * 主表film_audit，副表film、user,查询分页信息
     * @param status 审核状态
     * @param pageNo 页数
     * @param pageSize 页面大小
     * @return 审核电影分页信息
     */
    @Override
    public Map<String,Object> selectAuditPage(String status, Long pageNo, Long pageSize){
        Page<FilmAudit> page = new Page<>(pageNo,pageSize);

        // 主表查询条件 status
        LambdaQueryWrapper<FilmAudit> mainWrapper = Wrappers.lambdaQuery(FilmAudit.class).eq(StringUtils.hasLength(status), FilmAudit::getStatus, status);
        Page<FilmAudit> auditPage = this.page(page,mainWrapper);
        IPage<AuditFilmVo> auditFilmVoIPage = EntityUtils.toPage(auditPage, AuditFilmVo::new);

        // 获取连接查询的id，用于连接user表和film表
        Set<Integer> filmIds = EntityUtils.toSet(auditFilmVoIPage.getRecords(), AuditFilmVo::getFilmId);
        Set<Integer> userIds = EntityUtils.toSet(auditFilmVoIPage.getRecords(), FilmAudit::getUserId);

        if(userIds.size() > 0){
            // 副表查询条件 user表
            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> userList = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(userList, User::getUserId, e -> e);
            for (AuditFilmVo audit : auditFilmVoIPage.getRecords()) {
                User user = userMap.get(audit.getUserId());
                audit.setUserName(user.getUserName());
            }

        }

        // 副表查询条件 film
        if(filmIds.size() > 0){
            LambdaQueryWrapper<Film> filmWrapper = Wrappers.lambdaQuery(Film.class);
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> films = filmMapper.selectList(filmWrapper);
            Map<Integer, Film> filmMap = EntityUtils.toMap(films, Film::getFilmId, e -> e);
            for (AuditFilmVo audit : auditFilmVoIPage.getRecords()) {
                Film film = filmMap.get(audit.getFilmId());
                audit.setFilmName(film.getFilmName());
                audit.setDirector(film.getDirector());
                audit.setDuration(film.getDuration());
                audit.setFilmArea(film.getFilmArea());
                audit.setFilmCategory(film.getFilmCategory());
                audit.setFilmSummary(film.getFilmSummary());
                audit.setFilmUrl(film.getFilmUrl());
                audit.setImgUrl(film.getImgUrl());
                audit.setReleaseTime(film.getReleaseTime());
                audit.setStartingName(film.getStartingName());
            }

        }

        Comparator<AuditFilmVo> comparator = Comparator.comparing(AuditFilmVo::getAuditId);
        auditFilmVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", auditFilmVoIPage.getTotal());
        data.put("rows", auditFilmVoIPage.getRecords());
        return data;
    }

    /**
     * 查询出所有通过审核的电影
     * @return 审核通过的电影信息
     */
    public List<AuditFilmVo> selectAuditConfirmList(){


        // 主表查询条件 status
        LambdaQueryWrapper<FilmAudit> mainWrapper = Wrappers.lambdaQuery(FilmAudit.class).eq(FilmAudit::getStatus, "审核通过");
        List<FilmAudit> filmAudits = this.baseMapper.selectList(mainWrapper);
        List<AuditFilmVo> auditFilmVos = EntityUtils.toList(filmAudits, AuditFilmVo::new);

        // 获取连接查询的id，用于连接user表和film表
        Set<Integer> filmIds = EntityUtils.toSet(auditFilmVos, FilmAudit::getFilmId);
        Set<Integer> userIds = EntityUtils.toSet(auditFilmVos, FilmAudit::getUserId);

        if(userIds.size() > 0){
            // 副表查询条件 user表
            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> userList = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(userList, User::getUserId, e -> e);
            for (AuditFilmVo audit : auditFilmVos) {
                User user = userMap.get(audit.getUserId());
                audit.setUserName(user.getUserName());
            }

        }

        // 副表查询条件 film
        if(filmIds.size() > 0){
            LambdaQueryWrapper<Film> filmWrapper = Wrappers.lambdaQuery(Film.class);
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> films = filmMapper.selectList(filmWrapper);
            Map<Integer, Film> filmMap = EntityUtils.toMap(films, Film::getFilmId, e -> e);
            for (AuditFilmVo audit : auditFilmVos) {
                Film film = filmMap.get(audit.getFilmId());
                audit.setFilmName(film.getFilmName());
                audit.setDirector(film.getDirector());
                audit.setDuration(film.getDuration());
                audit.setFilmArea(film.getFilmArea());
                audit.setFilmCategory(film.getFilmCategory());
                audit.setFilmSummary(film.getFilmSummary());
                audit.setFilmUrl(film.getFilmUrl());
                audit.setImgUrl(film.getImgUrl());
                audit.setReleaseTime(film.getReleaseTime());
                audit.setStartingName(film.getStartingName());
            }

        }

        return auditFilmVos;
    }
}
