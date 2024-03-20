package com.design.filmr.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.design.filmr.common.vo.Result;
import com.design.filmr.sys.entity.Comment;
import com.design.filmr.sys.service.ICommentService;
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
@Api(tags = "评论管理")
@RestController
@RequestMapping("/comment")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ICommentService iCommentService;

    @ApiOperation("前台评论保存")
    @PostMapping
    public Result<?> saveComment(@RequestBody Comment comment){
        if(comment.getComment().equals("")){
            return Result.fail("评论不能为空");
        }
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getComment, comment.getComment());
        // 已存在相同评论
        if(iCommentService.count(wrapper) != 0){
            return Result.fail("已存在相同评论，请评论有营养的内容");
        }
        if(iCommentService.saveComment(comment)){
            return Result.success("评论成功");
        }
        return Result.fail("评论失败");
    }

    @ApiOperation("前台审核通过的评论展示")
    @GetMapping
    public Result<?> commentList(@RequestParam Integer filmId){
        Map<String, Object> map = iCommentService.commentList(filmId);
        if(map != null) {
            return Result.success(map);
        }
        return Result.fail("本电影暂时没有评论，欢迎留言哦~");
    }

    @ApiOperation("获取评论分页信息")
    @GetMapping("/list") // RequestParam 不用写这么多无关的字段进去 参数是用来参与查询的
    public Result<Map<String,Object>> selectCommentPage(@RequestParam(value = "userName", required = false) String userName,
                                                        @RequestParam(value = "pageNo") Long pageNo,
                                                        @RequestParam(value = "pageSize") Long pageSize) {
        // 可以根据userName评论人查询评论
        Map<String,Object> data = iCommentService.selectCommentPage(userName, pageNo, pageSize);
        return Result.success(data);
    }
    @ApiOperation("删除评论信息")
    @DeleteMapping("/{commentId}")
    public Result<?> deleteCommentById(@PathVariable("commentId") Integer commentId){
        logger.info("Id" + commentId);
        if(iCommentService.deleteComment(commentId)){
            return Result.success("删除评论成功");
        }
        return Result.fail("删除评论失败");
    }


}
