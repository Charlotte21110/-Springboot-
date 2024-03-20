package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author Gzee
 * @since 2023-10-13
 */
@TableName("like_film")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeFilm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "like_id", type = IdType.AUTO)
    private Integer likeId;

    /**
     * 电影id
     */
    private Integer filmId;

    private Integer count;

    /**
     * 是否热门推流
     */
    private Integer isPopular;

    // 构造器 用于多表操作
    public LikeFilm(LikeFilm likeFilm) {
        if(Objects.nonNull(likeFilm)){
            this.likeId = likeFilm.likeId;
            this.count = likeFilm.count;
            this.filmId = likeFilm.filmId;
            this.isPopular = likeFilm.isPopular;
        }
    }
}
