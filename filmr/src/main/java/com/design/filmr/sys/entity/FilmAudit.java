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
 * @since 2023-10-04
 */
@TableName("film_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilmAudit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "audit_id", type = IdType.AUTO)
    private Integer auditId;

    private Integer userId;

    private Integer filmId;

    private String status;

    private String auditInfo;

    // 构造器 用于多表操作
    public FilmAudit(FilmAudit filmAudit) {
        if(Objects.nonNull(filmAudit)){
            this.auditId = filmAudit.auditId;
            this.userId = filmAudit.userId;
            this.filmId = filmAudit.filmId;
            this.status = filmAudit.status;
            this.auditInfo = filmAudit.auditInfo;
        }
    }
}
