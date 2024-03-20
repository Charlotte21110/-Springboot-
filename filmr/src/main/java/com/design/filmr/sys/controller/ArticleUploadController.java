package com.design.filmr.sys.controller;

import com.design.filmr.common.vo.Result;
import com.design.filmr.common.vo.UploadArticleVo;
import com.design.filmr.sys.entity.ArticleUpload;
import com.design.filmr.sys.service.IArticleUploadService;
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
@Api(tags = "精选影评管理")
@RestController
@RequestMapping("/articleUpload")
public class ArticleUploadController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleUploadController.class);

    @Autowired
    private IArticleUploadService iArticleUploadService;

    @ApiOperation("查询已通过审核的影评分页信息")
    @GetMapping("/list")
    public Result<Map<String, Object>> getArticleUploadPage(@RequestParam(value = "uploadStatus", required = false) String uploadStatus,
                                                         @RequestParam(value = "pageNo") Long pageNo,
                                                         @RequestParam(value = "pageSize") Long pageSize) {

        Map<String, Object> data = iArticleUploadService.selectConfirmArticlePage(uploadStatus, pageNo, pageSize);
        return Result.success(data);
    }

    @ApiOperation("查询已通过审核并已上架的影评列表")
    @GetMapping("/confirmList")
    public Result<?> getConfirmList() {
        List<UploadArticleVo> data = iArticleUploadService.selectConfirmList();
        if(data != null){
            return Result.success(data);
        }
        return Result.fail("当前没有上架精选电影");
    }

    @ApiOperation("修改上架审核数据")
    @PutMapping
    public Result<?> uploadArticle(@RequestBody ArticleUpload articleUpload){
        if(iArticleUploadService.updateById(articleUpload)){
            return Result.success("修改上架数据成功");
        }
        return Result.success("修改上架数据失败");
    }

    @ApiOperation("获取article_upload")
    @GetMapping("/{uploadId}")
    public Result<?> getUploadArticleById(@PathVariable("uploadId") Integer uploadId){
        ArticleUpload articleUpload = iArticleUploadService.getById(uploadId);
        if(articleUpload != null){
            return Result.success(articleUpload);
        }
        return Result.fail("修改上架数据失败");
    }

}
