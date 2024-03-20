package com.design.filmr.sys.controller;


import com.design.filmr.common.vo.Result;
import com.design.filmr.common.vo.UploadFilmVo;
import com.design.filmr.sys.entity.FilmUpload;
import com.design.filmr.sys.service.IFilmUploadService;
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
 *  前端控制器
 * </p>
 *
 * @author Gzee
 * @since 2023-10-22
 */
@Api(tags = "精选电影管理")
@RestController
@RequestMapping("/filmUpload")
public class FilmUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FilmUploadController.class);

    @Autowired
    private IFilmUploadService iFilmUploadService;

    @ApiOperation("查询已通过审核的电影分页信息")
    @GetMapping("/list")
    public Result<Map<String, Object>> getFilmUploadPage(@RequestParam(value = "uploadStatus", required = false) String uploadStatus,
                                                   @RequestParam(value = "pageNo") Long pageNo,
                                                   @RequestParam(value = "pageSize") Long pageSize) {

        Map<String, Object> data = iFilmUploadService.selectConfirmFilmPage(uploadStatus, pageNo, pageSize);
        return Result.success(data);
    }

    @ApiOperation("查询已通过审核并已上架的电影列表")
    @GetMapping("/confirmList")
    public Result<?> getConfirmList() {
        List<UploadFilmVo> data = iFilmUploadService.selectConfirmList();
        if(data != null){
            return Result.success(data);
        }
        return Result.fail("当前没有上架精选电影");
    }

    @ApiOperation("修改上架审核数据")
    @PutMapping
    public Result<?> uploadFilm(@RequestBody FilmUpload filmUpload){
        if(iFilmUploadService.updateById(filmUpload)){
            return Result.success("修改上架数据成功");
        }
        return Result.success("修改上架数据失败");
    }

    @ApiOperation("获取uploadFilm")
    @GetMapping("/{uploadId}")
    public Result<?> getUploadCommentById(@PathVariable("uploadId") Integer uploadId){
        FilmUpload filmUpload = iFilmUploadService.getById(uploadId);
        if(filmUpload != null){
            return Result.success(filmUpload);
        }
        return Result.fail("修改上架数据失败");
    }
}
