package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.Film;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmQueryVo extends Film {
   
    // 审核表数据
    private Integer auditId;

    private Integer userId;

    private String status;

    private String auditInfo;
    // user表数据
    private String userName;


    public FilmQueryVo(Film film){
        super(film);
    }

}
