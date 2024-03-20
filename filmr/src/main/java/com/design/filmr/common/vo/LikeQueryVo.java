package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.LikeFilm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeQueryVo extends LikeFilm {
    // 电影表数据
    private String filmName;

    private String imgUrl;

    public LikeQueryVo(LikeFilm likeFilm){
        super(likeFilm);
    }
}
