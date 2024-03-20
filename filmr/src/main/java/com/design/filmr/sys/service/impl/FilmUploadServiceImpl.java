package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.common.vo.FilmQueryVo;
import com.design.filmr.common.vo.UploadFilmVo;
import com.design.filmr.sys.entity.Film;
import com.design.filmr.sys.entity.FilmAudit;
import com.design.filmr.sys.entity.FilmUpload;
import com.design.filmr.sys.entity.User;
import com.design.filmr.sys.mapper.FilmAuditMapper;
import com.design.filmr.sys.mapper.FilmMapper;
import com.design.filmr.sys.mapper.FilmUploadMapper;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IFilmUploadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * @since 2023-10-22
 */
@Service
public class FilmUploadServiceImpl extends ServiceImpl<FilmUploadMapper, FilmUpload> implements IFilmUploadService {

    @Resource
    private FilmAuditMapper filmAuditMapper;
    @Resource
    private FilmMapper filmMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String,Object> selectConfirmFilmPage(String uploadStatus, Long pageNo, Long pageSize){
        Page<FilmUpload> page = new Page<>(pageNo,pageSize);

        // 主表查询条件 uploadStatus
        LambdaQueryWrapper<FilmUpload> mainWrapper = Wrappers.lambdaQuery(FilmUpload.class).eq(StringUtils.hasLength(uploadStatus), FilmUpload::getUploadStatus, uploadStatus);

        Page<FilmUpload> uploadPage = this.page(page,mainWrapper);
        IPage<UploadFilmVo> uploadFilmVoIPage = EntityUtils.toPage(uploadPage, UploadFilmVo::new);

        // 获取连接查询的id，用于连接film_audit
        Set<Integer> auditIds = EntityUtils.toSet(uploadFilmVoIPage.getRecords(), UploadFilmVo::getAuditId);

        if(auditIds.size() > 0){
            // 副表查询条件 film_audit表
            LambdaQueryWrapper<FilmAudit> auditWrapper = Wrappers.lambdaQuery(FilmAudit.class);
            auditWrapper.in(FilmAudit::getAuditId, auditIds);
            List<FilmAudit> filmAuditList = filmAuditMapper.selectList(auditWrapper);
            Map<Integer, FilmAudit> auditMap = EntityUtils.toMap(filmAuditList, FilmAudit::getAuditId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadFilmVo uploadVo : uploadFilmVoIPage.getRecords()) {
                FilmAudit filmAudit = auditMap.get(uploadVo.getAuditId());
                uploadVo.setAuditInfo(filmAudit.getAuditInfo());
                uploadVo.setStatus(filmAudit.getStatus());
                uploadVo.setFilmId(filmAudit.getFilmId());
                uploadVo.setUserId(filmAudit.getUserId());
            }

            // 通过film_audit再关联film表和user表
            Set<Integer> filmIds = EntityUtils.toSet(uploadFilmVoIPage.getRecords(), UploadFilmVo::getFilmId);
            Set<Integer> userIds = EntityUtils.toSet(uploadFilmVoIPage.getRecords(), UploadFilmVo::getUserId);

            // 理论上上面判断语句进来后，程序正常运行一定有相应的film_audit表，换句话说一定有filmIds和userIds
            LambdaQueryWrapper<Film> filmWrapper = Wrappers.lambdaQuery(Film.class);
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> filmList = filmMapper.selectList(filmWrapper);
            Map<Integer, Film> filmMap = EntityUtils.toMap(filmList, Film::getFilmId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadFilmVo uploadVo : uploadFilmVoIPage.getRecords()) {
                Film film = filmMap.get(uploadVo.getFilmId());
                uploadVo.setFilmName(film.getFilmName());
                uploadVo.setDirector(film.getDirector());
                uploadVo.setDuration(film.getDuration());
                uploadVo.setFilmArea(film.getFilmArea());
                uploadVo.setFilmCategory(film.getFilmCategory());
                uploadVo.setFilmSummary(film.getFilmSummary());
                uploadVo.setFilmUrl(film.getFilmUrl());
                uploadVo.setImgUrl(film.getImgUrl());
                uploadVo.setReleaseTime(film.getReleaseTime());
                uploadVo.setStartingName(film.getStartingName());
            }

            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> userList = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(userList, User::getUserId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadFilmVo uploadVo : uploadFilmVoIPage.getRecords()) {
                User user = userMap.get(uploadVo.getUserId());
                uploadVo.setUserName(user.getUserName());
            }

        }

        Comparator<UploadFilmVo> comparator = Comparator.comparing(UploadFilmVo::getUploadId);
        uploadFilmVoIPage.getRecords().sort(comparator.reversed());

        Map<String,Object> data = new HashMap<>();
        data.put("total", uploadFilmVoIPage.getTotal());
        data.put("rows", uploadFilmVoIPage.getRecords());
        return data;
    }

    @Override
    public List<UploadFilmVo> selectConfirmList(){
        LambdaQueryWrapper<FilmUpload> mainWrapper = new LambdaQueryWrapper<>();
        mainWrapper.eq(FilmUpload::getUploadStatus, "已上架");
        List<FilmUpload> filmUploads = this.baseMapper.selectList(mainWrapper);

        // 获取连接查询的id，用于连接film_audit
        List<UploadFilmVo> uploadFilmVos = EntityUtils.toList(filmUploads, UploadFilmVo::new);
        Set<Integer> auditIds = EntityUtils.toSet(uploadFilmVos, FilmUpload::getAuditId);

        if(auditIds.size() > 0){
            // 副表查询条件 film_audit表
            LambdaQueryWrapper<FilmAudit> auditWrapper = Wrappers.lambdaQuery(FilmAudit.class);
            auditWrapper.in(FilmAudit::getAuditId, auditIds);
            List<FilmAudit> filmAuditList = filmAuditMapper.selectList(auditWrapper);
            Map<Integer, FilmAudit> auditMap = EntityUtils.toMap(filmAuditList, FilmAudit::getAuditId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadFilmVo uploadVo : uploadFilmVos) {
                FilmAudit filmAudit = auditMap.get(uploadVo.getAuditId());
                uploadVo.setAuditInfo(filmAudit.getAuditInfo());
                uploadVo.setStatus(filmAudit.getStatus());
                uploadVo.setFilmId(filmAudit.getFilmId());
                uploadVo.setUserId(filmAudit.getUserId());
            }

            // 通过film_audit再关联film表和user表
            Set<Integer> filmIds = EntityUtils.toSet(uploadFilmVos, UploadFilmVo::getFilmId);
            Set<Integer> userIds = EntityUtils.toSet(uploadFilmVos, UploadFilmVo::getUserId);

            // 理论上上面判断语句进来后，程序正常运行一定有相应的film_audit表，换句话说一定有filmIds和userIds
            LambdaQueryWrapper<Film> filmWrapper = Wrappers.lambdaQuery(Film.class);
            filmWrapper.in(Film::getFilmId, filmIds);
            List<Film> filmList = filmMapper.selectList(filmWrapper);
            Map<Integer, Film> filmMap = EntityUtils.toMap(filmList, Film::getFilmId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadFilmVo uploadVo : uploadFilmVos) {
                Film film = filmMap.get(uploadVo.getFilmId());
                uploadVo.setFilmName(film.getFilmName());
                uploadVo.setDirector(film.getDirector());
                uploadVo.setDuration(film.getDuration());
                uploadVo.setFilmArea(film.getFilmArea());
                uploadVo.setFilmCategory(film.getFilmCategory());
                uploadVo.setFilmSummary(film.getFilmSummary());
                uploadVo.setFilmUrl(film.getFilmUrl());
                uploadVo.setImgUrl(film.getImgUrl());
                uploadVo.setReleaseTime(film.getReleaseTime());
                uploadVo.setStartingName(film.getStartingName());
            }

            LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class);
            userWrapper.in(User::getUserId, userIds);
            List<User> userList = userMapper.selectList(userWrapper);
            Map<Integer, User> userMap = EntityUtils.toMap(userList, User::getUserId, e -> e);
            // 将查询出来的数据组装起来
            for (UploadFilmVo uploadVo : uploadFilmVos) {
                User user = userMap.get(uploadVo.getUserId());
                uploadVo.setUserName(user.getUserName());
            }

        }

        return uploadFilmVos;
    }
}
