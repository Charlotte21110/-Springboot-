package com.design.filmr.sys.controller;

import com.design.filmr.common.vo.Result;
import com.design.filmr.common.vo.UploadCommentVo;
import com.design.filmr.sys.entity.CommentUpload;
import com.design.filmr.sys.service.ICommentUploadService;
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
@Api(tags = "精选评论管理")
@RestController
@RequestMapping("/commentUpload")
public class CommentUploadController {
    private static final Logger logger = LoggerFactory.getLogger(CommentUploadController.class);

    @Autowired
    private ICommentUploadService iCommentUploadService;

    @ApiOperation("查询已通过审核的评论分页信息")
    @GetMapping("/list")
    public Result<Map<String, Object>> getCommentUploadPage(@RequestParam(value = "uploadStatus", required = false) String uploadStatus,
                                                         @RequestParam(value = "pageNo") Long pageNo,
                                                         @RequestParam(value = "pageSize") Long pageSize) {

        Map<String, Object> data = iCommentUploadService.selectConfirmCommentPage(uploadStatus, pageNo, pageSize);
        return Result.success(data);
    }

    @ApiOperation("查询已通过审核并已上架的评论列表")
    @GetMapping("/confirmList")
    public Result<?> getConfirmList() {
        List<UploadCommentVo> data = iCommentUploadService.selectConfirmList();
        if(data != null){
            return Result.success(data);
        }
        return Result.fail("当前没有上架精选电影");
    }

    @ApiOperation("修改上架审核数据")
    @PutMapping
    public Result<?> uploadComment(@RequestBody CommentUpload commentUpload){
        if(iCommentUploadService.updateById(commentUpload)){
            return Result.success("修改上架数据成功");
        }
        return Result.fail("修改上架数据失败");
    }

    @ApiOperation("获取uploadComment")
    @GetMapping("/{uploadId}")
    public Result<?> getUploadCommentById(@PathVariable("uploadId") Integer uploadId){
        CommentUpload commentUpload = iCommentUploadService.getById(uploadId);
        if(commentUpload != null){
            return Result.success(commentUpload);
        }
        return Result.fail("修改上架数据失败");
    }
}
