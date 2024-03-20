package com.design.filmr.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.common.vo.FilmQueryVo;
import com.design.filmr.sys.entity.*;
import com.design.filmr.sys.mapper.*;
import com.design.filmr.sys.service.ICommentService;
import com.design.filmr.sys.service.IFilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xin.altitude.cms.common.util.EntityUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FilmServiceImpl extends ServiceImpl<FilmMapper, Film> implements IFilmService {

    @Resource
    private FilmAuditMapper filmAuditMapper;
    @Resource
    private FilmMapper filmMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private LikeFilmMapper likeFilmMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private CommentAuditMapper commentAuditMapper;
    @Resource
    private FilmUploadMapper filmUploadMapper;
    @Resource
    private CommentUploadMapper commentUploadMapper;


    /**
     * 添加电影数据，同时添加审核表数据
     * @param filmQueryVo
     * @return
     */
    @Override
    public boolean saveFilm(FilmQueryVo filmQueryVo) {
        boolean flag1 = false;
        boolean flag2 = false;

        // film表添加
        Film film = new Film();
        FilmAudit filmAudit = new FilmAudit();

        film.setDirector(filmQueryVo.getDirector());film.setDuration(filmQueryVo.getDuration());
        film.setFilmArea(filmQueryVo.getFilmArea());film.setFilmCategory(filmQueryVo.getFilmCategory());
        film.setStartingName(filmQueryVo.getStartingName());film.setReleaseTime(filmQueryVo.getReleaseTime());
        film.setFilmName(filmQueryVo.getFilmName());film.setFilmSummary(filmQueryVo.getFilmSummary());
        film.setFilmUrl(filmQueryVo.getFilmUrl());film.setImgUrl(filmQueryVo.getImgUrl());

        flag1 = filmMapper.insert(film) > 0;

        // film_audit 添加
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 前端非空UserName
        wrapper.eq(StringUtils.hasLength(filmQueryVo.getUserName()),User::getUserName, filmQueryVo.getUserName());

        // filmUrl标识一个电影，由此获取新增的电影(新增电影id是自增的）
        LambdaQueryWrapper<Film> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(StringUtils.hasLength(filmQueryVo.getFilmUrl()), Film::getFilmUrl, filmQueryVo.getFilmUrl());
        Film targetFilm = filmMapper.selectOne(wrapper1);

        filmAudit.setFilmId(targetFilm.getFilmId());
        filmAudit.setUserId(userMapper.selectOne(wrapper).getUserId());
        filmAudit.setStatus("待审核");

        flag2 = filmAuditMapper.insert(filmAudit) > 0;

        return flag1 && flag2;

    }

    /**
     * 删除film表数据，同时删除对应审核表数据
     * @param filmId
     * @return
     */
    public boolean deleteFilm(Integer filmId){
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = true;
        boolean flag4 = true;
        boolean flag5 = true;
        // 删除电影表数据
        flag1 = filmMapper.deleteById(filmId) > 0;

        // 删除审核表数据
        LambdaQueryWrapper<FilmAudit> filmAuditWrapper = new LambdaQueryWrapper<>();
        filmAuditWrapper.eq(filmId != null, FilmAudit::getFilmId, filmId);
        FilmAudit filmAudit = filmAuditMapper.selectOne(filmAuditWrapper);
        flag2 = filmAuditMapper.deleteById(filmAudit.getAuditId()) > 0;
        if(filmAudit.getStatus().equals("审核通过")){
            // 电影上架首页表 film_upload信息删除
            LambdaQueryWrapper<FilmUpload> filmUploadWrapper = new LambdaQueryWrapper<>();
            filmUploadWrapper.eq(FilmUpload::getAuditId, filmAudit.getAuditId());
            flag2 = flag2 && (filmUploadMapper.delete(filmUploadWrapper) > 0);
        }

        // 删除点赞表数据
        Integer likeId = likeFilmMapper.getLikeIdByFilmId(filmId);
        if(likeId != null){
            flag3 = likeFilmMapper.deleteById(likeFilmMapper.getLikeIdByFilmId(filmId)) > 0;
        }

        // 删除评论表数据
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(filmId != null, Comment::getFilmId, filmId);
        List<Comment> comments = commentMapper.selectList(wrapper);
        // 获取comments中的commentId
        Set<Integer> commentIds = EntityUtils.toSet(comments, Comment::getCommentId);
        if(commentIds.size() > 0){
            LambdaQueryWrapper<Comment> deleteWrapper = Wrappers.lambdaQuery(Comment.class);
            deleteWrapper.in(Comment::getCommentId, commentIds);
            flag4 = commentMapper.delete(deleteWrapper) > 0;

            // 删除评论审核表信息
            LambdaQueryWrapper<CommentAudit> deleteAuditWrapper = Wrappers.lambdaQuery(CommentAudit.class);
            deleteAuditWrapper.in(CommentAudit::getCommentId, commentIds);

            LambdaQueryWrapper<CommentAudit> deleteUploadWrapper = new LambdaQueryWrapper<>();
            deleteUploadWrapper.in(CommentAudit::getCommentId, commentIds);
            deleteUploadWrapper.eq(CommentAudit::getStatus, "审核通过");

            List<CommentAudit> commentAudits = commentAuditMapper.selectList(deleteUploadWrapper);
            List<Integer> audits = EntityUtils.toList(commentAudits, CommentAudit::getAuditId);

            // 删除精选上架评论表信息
            LambdaQueryWrapper<CommentUpload> uploadWrapper = new LambdaQueryWrapper<>();
            uploadWrapper.in(!audits.isEmpty(), CommentUpload::getAuditId, audits);

            flag5 = commentAuditMapper.delete(deleteAuditWrapper) > 0;

            flag5 = flag5 && (commentUploadMapper.delete(uploadWrapper) > 0);

        }

        return flag1 && flag2 && flag3 && flag4 && flag5;
    }

    /**
     * 主表Film 副表film_audit 查询出副表status和audit_info数据，总查询两次
     * @param filmName 查询条件 电影名
     * @param filmArea 查询条件 电影地区
     * @param filmCategory 查询条件 电影风格
     * @param pageNo 分页数据
     * @param pageSize 分页数据
     * @return
     */
    public Map<String,Object> selectFilmPage(String filmName, String filmArea, String filmCategory, Long pageNo, Long pageSize){
        Page<Film> page = new Page<>(pageNo,pageSize);

        // 查询条件
        LambdaQueryWrapper<Film> wrapper = Wrappers.lambdaQuery(Film.class);
        // 电影名模糊查询 StringUtils.hasLength() 判断String值是否为空 为空就不查询该条件
        wrapper.like(StringUtils.hasLength(filmName), Film::getFilmName, filmName);
        wrapper.eq(StringUtils.hasLength(filmArea), Film::getFilmArea, filmArea);
        wrapper.eq(StringUtils.hasLength(filmCategory), Film::getFilmCategory, filmCategory);

        Page<Film> filmPage = this.page(page,wrapper);

        IPage<FilmQueryVo> filmQueryVoIPage = EntityUtils.toPage(filmPage, FilmQueryVo::new);
        // 获取多表中filmId的值
        Set<Integer> filmIds = EntityUtils.toSet(filmQueryVoIPage.getRecords(), FilmQueryVo::getFilmId);

        // 非空就查询
        if(filmIds.size() > 0){
            LambdaQueryWrapper<FilmAudit> filmAuditLambdaQueryWrapper = Wrappers.lambdaQuery(FilmAudit.class).in(FilmAudit::getFilmId,filmIds);
            List<FilmAudit> auditList = filmAuditMapper.selectList(filmAuditLambdaQueryWrapper);
            Map<Integer, FilmAudit> map = EntityUtils.toMap(auditList, FilmAudit::getFilmId, e -> e);
            for (FilmQueryVo f : filmQueryVoIPage.getRecords()) {
                FilmAudit audit = map.get(f.getFilmId());
                f.setStatus(audit.getStatus());
                f.setAuditInfo(audit.getAuditInfo());
                f.setAuditId(audit.getAuditId());
                f.setUserId(audit.getUserId());
                f.setAuditId(audit.getAuditId());

            }

            // 先获取userId集合，再一次查询
            Set<Integer> userIds = EntityUtils.toSet(auditList, FilmAudit::getUserId);

            LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.lambdaQuery(User.class).in(User::getUserId, userIds);
            List<User> users = userMapper.selectList(userLambdaQueryWrapper);
            Map<Integer, User> integerUserMap = EntityUtils.toMap(users, User::getUserId, e -> e);

            for (FilmQueryVo f : filmQueryVoIPage.getRecords()) {
                User user = integerUserMap.get(f.getUserId());
                f.setUserName(user.getUserName());
            }

        }

        Comparator<FilmQueryVo> comparator = Comparator.comparing(FilmQueryVo::getFilmId);
        filmQueryVoIPage.getRecords().sort(comparator.reversed());


        Map<String,Object> filmInfo = new HashMap<>();
        filmInfo.put("total", filmQueryVoIPage.getTotal());
        filmInfo.put("rows", filmQueryVoIPage.getRecords());

        return filmInfo;
    }

    /**
     * 修改film表数据，同时将status重置为`待审核`，auditInfo清空，删除上架审核表信息
     * @param filmQueryVo 电影查询表现层
     * @return 是否成功，n个flag方便定位出错位置
     */
    public boolean updateFilm(FilmQueryVo filmQueryVo){
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = true;

        Film film = new Film();

        film.setDirector(filmQueryVo.getDirector());film.setDuration(filmQueryVo.getDuration());
        film.setFilmArea(filmQueryVo.getFilmArea());film.setFilmCategory(filmQueryVo.getFilmCategory());
        film.setStartingName(filmQueryVo.getStartingName());film.setReleaseTime(filmQueryVo.getReleaseTime());
        film.setFilmName(filmQueryVo.getFilmName());film.setFilmSummary(filmQueryVo.getFilmSummary());
        film.setFilmUrl(filmQueryVo.getFilmUrl());film.setImgUrl(filmQueryVo.getImgUrl());
        film.setFilmId(filmQueryVo.getFilmId());

        flag1 = filmMapper.updateById(film) > 0;


        // 审核表修改
        LambdaQueryWrapper<FilmAudit> queryWrapper = new LambdaQueryWrapper<FilmAudit>();
        queryWrapper.eq(FilmAudit::getFilmId, film.getFilmId());
        FilmAudit filmAudit = filmAuditMapper.selectOne(queryWrapper);

        // 更新后重置审核信息，需要重新审核
        filmAudit.setAuditInfo("");
        filmAudit.setStatus("待审核");
        flag2 = filmAuditMapper.updateById(filmAudit) > 0;

        // 删除电影上架首页审核信息
        LambdaQueryWrapper<FilmUpload> filmUploadLambdaQueryWrapper = new LambdaQueryWrapper<>();
        filmUploadLambdaQueryWrapper.eq(FilmUpload::getAuditId, filmAudit.getAuditId());
        FilmUpload filmUpload = filmUploadMapper.selectOne(filmUploadLambdaQueryWrapper);
        if(filmUpload != null){
            flag3 = filmUploadMapper.delete(filmUploadLambdaQueryWrapper) > 0;
        }
        // 删除点赞表信息
        LambdaQueryWrapper<LikeFilm> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(LikeFilm::getFilmId, film.getFilmId());
        LikeFilm likeFilm = likeFilmMapper.selectOne(likeWrapper);
        if(likeFilm != null){
            likeFilmMapper.deleteById(likeFilm.getLikeId());
        }

        return flag1 && flag2 && flag3;
    }
}
