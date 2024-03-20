package com.design.filmr.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.design.filmr.common.vo.Result;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.Film;
import com.design.filmr.sys.entity.LikeFilm;
import com.design.filmr.sys.service.ILikeFilmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
@Api(tags = "点赞管理")
@RestController
@RequestMapping("/like")
public class LikeFilmController {
    private static final Logger logger = LoggerFactory.getLogger(LikeFilmController.class);

    @Autowired
    private ILikeFilmService iLikeFilmService;


    /**
     * 根据传进来的isPopular查询相应点赞表信息
     *
     * @param isPopular
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation("获取点赞分页信息")
    @GetMapping("/list")
    public Result<Map<String, Object>> getLikePage(@RequestParam(value = "isPopular", required = false) Integer isPopular,
                                                   @RequestParam(value = "pageNo") Long pageNo,
                                                   @RequestParam(value = "pageSize") Long pageSize) {
        // 模糊查询电影名查询电影
        Map<String, Object> data = iLikeFilmService.selectLikePage(isPopular, pageNo, pageSize);
        return Result.success(data);
    }

    @ApiOperation("获取已推流的电影列表信息")
    @GetMapping("/popList")
    public Result<List<Film>> getPopList() {
        List<Film> data = iLikeFilmService.selectPopFilm();
        if(data != null){
            return Result.success(data);
        }
        return Result.fail("当前没有热门电影");
    }


    @ApiOperation("点赞操作")
    @PutMapping("/count")
    @AuthCheck
    public Result<?> updateLikeInfo(@RequestParam(value = "filmId") Integer filmId) {
        try {
            iLikeFilmService.updateLikeData(filmId);
            return Result.success("点赞成功");
        } catch (Exception e) {
            return Result.fail("点赞操作失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新推流信息")
    @PutMapping
    public Result<?> updatePopInfo(@RequestBody LikeFilm likeFilm) {
        if (iLikeFilmService.makePop(likeFilm.getFilmId(), likeFilm.getIsPopular())) {
            return Result.success("更新热门信息成功");
        }
        return Result.fail("更新热门信息失败");
    }

    @ApiOperation("通过likeId获取likeFilm")
    @GetMapping("/{likeId}")
    public Result<LikeFilm> getLikeFilmById(@PathVariable("likeId") Integer likeId) {
        logger.info("Id" + likeId);
        LikeFilm likeFilm = iLikeFilmService.getById(likeId);
        logger.info("like info found: " + likeFilm);
        return Result.success(likeFilm);
    }

    @ApiOperation("通过filmId获取likeFilm")
    @GetMapping("/filmId/{filmId}")
    public Result<LikeFilm> getLikeFilmByFilmId(@PathVariable("filmId") Integer filmId) {
        logger.info("Id" + filmId);

        LambdaQueryWrapper<LikeFilm> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(LikeFilm::getFilmId, filmId);
        LikeFilm likeFilm = iLikeFilmService.getOne(likeWrapper);
        logger.info("like info found: " + likeFilm);
        return Result.success(likeFilm);
    }

}
