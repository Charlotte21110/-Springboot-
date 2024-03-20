package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Gzee
 * @since 2023-10-22
 */
@TableName("film_upload")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmUpload implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 上架id
     */
    @TableId(value = "upload_id", type = IdType.AUTO)
    private Integer uploadId;

    /**
     * 审核电影id
     */
    private Integer auditId;

    /**
     * 上架状态
     */
    private String uploadStatus;

    // 构造器 用于多表操作
    public FilmUpload(FilmUpload filmUpload){
        this.uploadId = filmUpload.uploadId;
        this.auditId = filmUpload.auditId;
        this.uploadStatus = filmUpload.uploadStatus;
    }

}
