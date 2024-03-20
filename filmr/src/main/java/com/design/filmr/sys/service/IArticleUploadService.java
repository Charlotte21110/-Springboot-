package com.design.filmr.sys.service;

import com.design.filmr.common.vo.UploadArticleVo;
import com.design.filmr.sys.entity.ArticleUpload;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-22
 */
public interface IArticleUploadService extends IService<ArticleUpload> {
    Map<String,Object> selectConfirmArticlePage(String uploadStatus, Long pageNo, Long pageSize);

    List<UploadArticleVo> selectConfirmList();
}
