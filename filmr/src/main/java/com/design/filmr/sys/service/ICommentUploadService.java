package com.design.filmr.sys.service;

import com.design.filmr.common.vo.UploadCommentVo;
import com.design.filmr.sys.entity.CommentUpload;
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
public interface ICommentUploadService extends IService<CommentUpload> {

    Map<String,Object> selectConfirmCommentPage(String uploadStatus, Long pageNo, Long pageSize);

    List<UploadCommentVo> selectConfirmList();
}
