package com.design.filmr.common.vo;

import com.design.filmr.sys.entity.CommentAudit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentAuditVo extends CommentAudit {
    // comment表数据
    private String userName;

    private String comment;

    public CommentAuditVo(CommentAudit commentAudit){
        super(commentAudit);
    }

}
