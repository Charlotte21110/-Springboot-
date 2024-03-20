package com.design.filmr.sys.controller;


import com.design.filmr.bean.LocalUser;
import com.design.filmr.common.vo.FilmQueryVo;
import com.design.filmr.common.vo.Result;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.Film;
import com.design.filmr.sys.entity.User;
import com.design.filmr.sys.service.IFilmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Gzee
 * @since 2023-09-29
 */
@Api(tags = "影视管理")
@RestController
@RequestMapping("/film")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    private IFilmService iFilmService;

    /**
     * 新增
     * @param filmQueryVo
     * @return
     */
    @ApiOperation("影视新增")
    @PostMapping
    @AuthCheck
    public Result<?> addFilm(@RequestBody FilmQueryVo filmQueryVo){
        User user = LocalUser.getUser();
        filmQueryVo.setUserName(user.getUserName());
        if(iFilmService.saveFilm(filmQueryVo))
            return Result.success("新增电影成功");
        return Result.fail("新增失败");
    }

    /**
     * 根据filmId删除
     * @param filmId
     * @return
     */
    @ApiOperation("影视删除")
    @DeleteMapping("/{filmId}")
    public Result<?> deleteFilmById(@PathVariable("filmId") Integer filmId){
        logger.info("Id" + filmId);
        logger.info("Film found: " + filmId);
        iFilmService.deleteFilm(filmId);
        return Result.success("删除数据成功");
    }


    /**
     * select
     * 查询film表信息 get请求 展示信息：filmId,imgUrl,userName,filmName,filmCategory,filmArea,status,auditInfo
     * @param filmName
     * @param filmArea
     * @param filmCategory
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation("获取影视分页信息")
    @GetMapping("/list") // RequestParam 不用写这么多无关的字段进去 参数是用来参与查询的
    public Result<Map<String,Object>> getFilmPage(@RequestParam(value = "filmName", required = false) String filmName,
                                               @RequestParam(value = "filmArea", required = false) String filmArea,
                                               @RequestParam(value = "filmCategory", required = false) String filmCategory,
                                               @RequestParam(value = "pageNo") Long pageNo,
                                               @RequestParam(value = "pageSize") Long pageSize) {
        // 根据电影地区、风格和模糊查询电影名查询电影
        Map<String,Object> data;
        data = iFilmService.selectFilmPage(filmName, filmArea, filmCategory, pageNo, pageSize);
        return Result.success(data);
    }


    @ApiOperation("更新电影")
    @PutMapping
    public  Result<?> updateFilm(@RequestBody FilmQueryVo filmQueryVo){
        iFilmService.updateFilm(filmQueryVo);
        return Result.success("修改电影成功");
    }

    @ApiOperation("获取电影ID")
    @GetMapping("/{filmId}")
    public Result<Film> getFilmById(@PathVariable("filmId") Integer filmId){
        logger.info("Id" + filmId);
        Film film = iFilmService.getById(filmId);
        logger.info("Film found: " + film);
        return Result.success(film);
    }



}
