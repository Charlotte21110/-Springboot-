package com.design.filmr.sys.service;

import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.common.vo.UploadFilmVo;
import com.design.filmr.sys.entity.FilmUpload;
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
public interface IFilmUploadService extends IService<FilmUpload> {

    Map<String,Object> selectConfirmFilmPage(String uploadStatus, Long pageNo, Long pageSize);

    List<UploadFilmVo> selectConfirmList();

}
