package com.design.filmr.sys.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.design.filmr.bean.LocalUser;
import com.design.filmr.common.vo.ArticleVo;
import com.design.filmr.common.vo.Result;
import com.design.filmr.interceptor.AuthCheck;
import com.design.filmr.sys.entity.Article;
import com.design.filmr.sys.entity.ArticleAudit;
import com.design.filmr.sys.entity.User;
import com.design.filmr.sys.service.IArticleAuditService;
import com.design.filmr.sys.service.IArticleService;
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
@Api(tags = "影评管理")
@RestController
@RequestMapping("/article")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IArticleService iArticleService;
    @Autowired
    private IArticleAuditService iArticleAuditService;
    @Autowired
    private IArticleUploadService iArticleUploadService;

    @ApiOperation("影评新增")
    @AuthCheck
    @PostMapping
    public Result<?> addArticle(@RequestBody Article article){
        if(article.getFilmName().length() > 20 || article.getTitle().length() > 20){
            return Result.fail("输入信息过长");
        }
        User currentUser = LocalUser.getUser();
        article.setUserId(currentUser.getUserId());
        article.setTime(DateUtil.now());
        if(iArticleService.saveArticle(article, currentUser.getUserId()))
            return Result.success("新增影评成功");
        return Result.fail("新增失败");
    }


    @ApiOperation("影评删除")
    @DeleteMapping("/{articleId}")
    public Result<?> deleteArticleById(@PathVariable("articleId") Integer articleId){
        logger.info("Id" + articleId);
        if(iArticleService.deleteArticle(articleId)){
            return Result.success("删除数据成功");
        }
        return Result.fail("删除数据");
    }

    @ApiOperation("获取影评分页信息")
    @GetMapping("/list") // RequestParam 不用写这么多无关的字段进去 参数是用来参与查询的
    public Result<Map<String,Object>> selectArticlePage(@RequestParam(value = "title", required = false) String title,
                                                        @RequestParam(value = "filmName", required = false) String filmName,
                                                        @RequestParam(value = "pageNo") Long pageNo,
                                                        @RequestParam(value = "pageSize") Long pageSize) {
        // 根据电影地区、风格和模糊查询电影名查询电影
        Map<String,Object> data = iArticleService.selectArticlePage(title, filmName, pageNo, pageSize);
        return Result.success(data);
    }

    @ApiOperation("获取审核通过的影评信息")
    @GetMapping("confirmList")
    public Result<List<ArticleVo>> selectArticleList(){
        List<ArticleVo> articles = iArticleService.selectArticleList();
        if(!articles.isEmpty()){
            return Result.success(articles);
        }
        return Result.fail();
    }

    @ApiOperation("更新影评信息")
    @PutMapping
    @AuthCheck
    public Result<?> updateArticle(@RequestBody Article article){
        User currentUser = LocalUser.getUser();
        article.setUserId(currentUser.getUserId());
        if(iArticleService.updateArticle(article)){
            return Result.success("修改影评成功");
        }
        return Result.fail("修改影评失败");
    }


    @ApiOperation("通过articleId获取article")
    @GetMapping("/{articleId}")
    public Result<Article> getArticleById(@PathVariable("articleId") Integer articleId){
        logger.info("Id" + articleId);
        Article article = iArticleService.getById(articleId);
        logger.info("Film found: " + article);
        return Result.success(article);
    }
}
