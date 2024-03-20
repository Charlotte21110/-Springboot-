package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.FilmAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditFilmVo extends FilmAudit {

    // film表数据
    private String filmName;

    private String filmSummary;

    private String filmUrl;

    private String filmCategory;

    private String imgUrl;

    private String filmArea;

    private String director;

    private String startingName;

    private String duration;

    private Date releaseTime;

    // 用户表数据
    private String userName;

    public AuditFilmVo(FilmAudit filmAudit){
        super(filmAudit);
    }

}
