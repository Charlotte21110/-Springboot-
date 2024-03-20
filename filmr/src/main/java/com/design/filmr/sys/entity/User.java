package com.design.filmr.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author Marisa
 * @since 2023-09-16
 */
//@TableName("/user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer userId;

    private String userName;

    private String userAccount;

    private String password;

    private String description;

    private String gender;

    private String indexUrl;

    private Integer vip;

    private Integer artist;
    @TableField(exist = false)
    private List<Integer> roleIdList;

}
