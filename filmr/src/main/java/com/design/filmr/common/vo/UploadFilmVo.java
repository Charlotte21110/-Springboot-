package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.FilmUpload;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadFilmVo extends FilmUpload {

    // 审核表信息
    private Integer userId;

    private Integer filmId;

    private String status;

    private String auditInfo;

    // 电影表信息
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

    // user表信息
    private String userName;

    public UploadFilmVo(FilmUpload filmUpload){
        super(filmUpload);
    }
}
