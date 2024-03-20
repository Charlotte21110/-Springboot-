package com.design.filmr.sys.controller;

import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.common.vo.Result;
import com.design.filmr.sys.entity.FilmAudit;
import com.design.filmr.sys.entity.FilmUpload;
import com.design.filmr.sys.entity.LikeFilm;
import com.design.filmr.sys.service.IFilmAuditService;
import com.design.filmr.sys.service.IFilmUploadService;
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
 *  前端控制器
 * </p>
 *
 * @author Gzee
 * @since 2023-10-04
 */
@Api(tags = "影视审核管理")
@RestController
@RequestMapping("/audit")
public class FilmAuditController {

    private static final Logger logger = LoggerFactory.getLogger(FilmAuditController.class);

    @Autowired
    private IFilmAuditService iFilmAuditService;
    @Autowired
    private IFilmUploadService iFilmUploadService;
    @Autowired
    private ILikeFilmService iLikeFilmService;

    @ApiOperation("获取审核列表")
    @GetMapping("/list")
    public Result<Map<String,Object>> getAuditList(
            @RequestParam(value="status",required = false) String status,
            @RequestParam(value="pageNo") Long pageNo,
            @RequestParam(value="pageSize") Long pageSize) {

        Map<String, Object> auditInfo = this.iFilmAuditService.selectAuditPage(status,pageNo,pageSize);
        if(auditInfo != null)
            return Result.success(auditInfo);
        return Result.fail("查询失败");
    }

    @ApiOperation("修改审核数据")
    @PutMapping
    public Result<?> auditFilm(@RequestBody FilmAudit filmAudit){
        if(iFilmAuditService.updateById(filmAudit)){
            // 审核通过后就添加电影上架首页信息
            if(filmAudit.getStatus().equals("审核通过")){
                FilmUpload filmUpload = new FilmUpload();
                filmUpload.setAuditId(filmAudit.getAuditId());
                filmUpload.setUploadStatus("未上架");
                iFilmUploadService.save(filmUpload);

                // 点赞表添加
                LikeFilm likeFilm = new LikeFilm();
                likeFilm.setCount(0);
                likeFilm.setIsPopular(0);
                likeFilm.setFilmId(filmAudit.getFilmId());
                iLikeFilmService.save(likeFilm);
            }
            return Result.success("修改审核数据成功");
        }
        return Result.success("修改审核数据失败");
    }

    @ApiOperation("获取审核的ID名")
    @GetMapping("/{auditId}")
    public Result<FilmAudit> getAuditById(@PathVariable("auditId") Integer auditId){
        logger.info("Id" + auditId);
        FilmAudit filmAudit = iFilmAuditService.getById(auditId);

        if(filmAudit != null){
            logger.info("auditInfo found: " + filmAudit);
            return Result.success(filmAudit);
        }
        return Result.fail("查询失败");
    }


    @ApiOperation("获取审核通过的电影列表")
    @GetMapping("/confirmList")
    public Result<List<AuditFilmVo>> getAuditList() {
        List<AuditFilmVo> auditInfo = this.iFilmAuditService.selectAuditConfirmList();
        if(auditInfo != null){
            logger.info("auditInfo found: " + auditInfo);
            return Result.success(auditInfo);
        }
        return Result.fail("查询失败");
    }

}
