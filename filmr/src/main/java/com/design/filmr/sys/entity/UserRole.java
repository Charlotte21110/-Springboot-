package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
@TableName("user_role")
@Data
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer roleId;

    private String roleDesc;

}
