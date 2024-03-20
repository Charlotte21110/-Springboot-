package com.design.filmr.sys.controller;

import com.design.filmr.common.vo.ArticleAuditVo;
import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.common.vo.Result;
import com.design.filmr.sys.entity.ArticleAudit;
import com.design.filmr.sys.entity.ArticleUpload;
import com.design.filmr.sys.service.IArticleAuditService;
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
 * @since 2023-10-15
 */
@Api(tags = "影评审核")
@RestController
@RequestMapping("/articleAudit")
public class ArticleAuditController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleAuditController.class);

    @Autowired
    private IArticleAuditService iArticleAuditService;
    @Autowired
    private IArticleUploadService iArticleUploadService;

    @ApiOperation("获取影评审核分页信息")
    @GetMapping("/list")
    public Result<Map<String,Object>> getAuditList(
            @RequestParam(value="status",required = false) String status,
            @RequestParam(value="pageNo") Long pageNo,
            @RequestParam(value="pageSize") Long pageSize) {

        Map<String, Object> auditInfo = this.iArticleAuditService.selectAuditPage(status,pageNo,pageSize);
        if(auditInfo != null)
            return Result.success(auditInfo);
        return Result.fail("查询失败");
    }

    @ApiOperation("获取审核通过的影评列表")
    @GetMapping("/confirmList")
    public Result<List<ArticleAuditVo>> getAuditList() {
        List<ArticleAuditVo> auditInfo = this.iArticleAuditService.selectAuditConfirmList();
        if(auditInfo != null){
            logger.info("auditInfo found: " + auditInfo);
            return Result.success(auditInfo);
        }
        return Result.fail("查询失败");
    }

    @ApiOperation("修改审核数据")
    @PutMapping
    public Result<?> auditArticle(@RequestBody ArticleAudit articleAudit){
        if(iArticleAuditService.updateById(articleAudit)){
            if(articleAudit.getStatus().equals("审核通过")){
                ArticleUpload articleUpload = new ArticleUpload();
                articleUpload.setAuditId(articleAudit.getAuditId());
                articleUpload.setUploadStatus("未上架");
                iArticleUploadService.save(articleUpload);
            }
            return Result.success("修改审核数据成功");
        }

        return Result.success("修改审核数据失败");
    }

    @ApiOperation("获取审核的ID名")
    @GetMapping("/{auditId}")
    public Result<ArticleAudit> getAuditById(@PathVariable("auditId") Integer auditId){
        logger.info("Id" + auditId);
        ArticleAudit ArticleAudit = iArticleAuditService.getById(auditId);
        logger.info("auditInfo found: " + ArticleAudit);
        return Result.success(ArticleAudit);
    }
}
//测试