package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author Gzee
 * @since 2023-09-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    @TableId(value = "film_id", type = IdType.AUTO)
    private Integer filmId;

    /**
     * 电影名称
     */
    private String filmName;

    /**
     * 电影简介
     */
    private String filmSummary;

    /**
     * 电影链接
     */
    private String filmUrl;

    /**
     * 电影分类
     */
    private String filmCategory;

    /**
     * 封面
     */
    private String imgUrl;

    /**
     * 区域
     */
    private String filmArea;

    /**
     * 导演
     */
    private String director;

    /**
     * 主演 数据库不能直接用starting
     */
    private String startingName;

    /**
     * 时长
     */
    private String duration;

    /**
     * 上映时间
     */
    private Date releaseTime;


    // 构造器 用于多表操作
    public Film(Film film) {
        if(Objects.nonNull(film)){
            this.filmId = film.filmId;
            this.filmName = film.filmName;
            this.filmSummary = film.filmSummary;
            this.filmUrl = film.filmUrl;
            this.filmCategory = film.filmCategory;
            this.imgUrl = film.imgUrl;
            this.filmArea = film.filmArea;
            this.director = film.director;
            this.startingName = film.startingName;
            this.duration = film.duration;
            this.releaseTime = film.releaseTime;
        }
    }
}
