package com.design.filmr.sys.controller;


import com.design.filmr.common.vo.Result;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.CommentAudit;
import com.design.filmr.sys.entity.CommentUpload;
import com.design.filmr.sys.service.ICommentAuditService;
import com.design.filmr.sys.service.ICommentUploadService;
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
 * @since 2023-10-15
 */
@Api(tags = "评论审核")
@RestController
@RequestMapping("/commentAudit")
public class CommentAuditController {
    private static final Logger logger = LoggerFactory.getLogger(CommentAuditController.class);

    @Autowired
    private ICommentAuditService iCommentAuditService;
    @Autowired
    private ICommentUploadService iCommentUploadService;

    @ApiOperation("获取审核列表")
    @GetMapping("/list")
    public Result<Map<String,Object>> getAuditList(
            @RequestParam(value="status",required = false) String status,
            @RequestParam(value="pageNo") Long pageNo,
            @RequestParam(value="pageSize") Long pageSize) {

        Map<String, Object> auditInfo = iCommentAuditService.selectAuditPage(status,pageNo,pageSize);
        if(auditInfo != null)
            return Result.success(auditInfo);
        return Result.fail("查询失败");
    }

    @ApiOperation("修改审核数据")
    @PutMapping
    public Result<?> auditArticle(@RequestBody CommentAudit commentAudit){

        if(iCommentAuditService.updateById(commentAudit)){
            // 审核通过就添加精选上架审核信息
            if(commentAudit.getStatus().equals("审核通过")){
                CommentUpload commentUpload = new CommentUpload();
                commentUpload.setAuditId(commentAudit.getAuditId());
                commentUpload.setUploadStatus("未上架");
                iCommentUploadService.save(commentUpload);
            }
            return Result.success("修改审核数据成功");
        }
        return Result.success("修改审核数据失败");
    }

    @ApiOperation("通过审核id获取评论审核信息")
    @GetMapping("/{auditId}")
    public Result<CommentAudit> getAuditById(@PathVariable("auditId") Integer auditId){
        logger.info("Id" + auditId);
        CommentAudit commentAudit = iCommentAuditService.getById(auditId);
        logger.info("auditInfo found: " + commentAudit);
        return Result.success(commentAudit);
    }
}
