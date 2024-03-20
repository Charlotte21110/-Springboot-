package com.design.filmr.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.sys.entity.FilmAudit;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Gzee
 * @since 2023-10-04
 */
public interface IFilmAuditService extends IService<FilmAudit> {
    Map<String,Object> selectAuditPage(String status, Long pageNo, Long pageSize);

    List<AuditFilmVo> selectAuditConfirmList();
}
