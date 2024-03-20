package com.design.filmr;

import com.design.filmr.common.vo.AuditFilmVo;
import com.design.filmr.sys.mapper.FilmAuditMapper;
import com.design.filmr.sys.mapper.UserMapper;
import com.design.filmr.sys.service.IFilmAuditService;
import com.design.filmr.sys.service.IFilmService;
import com.design.filmr.sys.service.IFilmUploadService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@MapperScan("com.design.filmr.sys.mapper")
class FilmrApplicationTests {

    @Autowired
    IFilmUploadService iFilmUploadService;

    @Test
    void testMapper(){
        iFilmUploadService.selectConfirmFilmPage("", 1L, 10L);
    }
    @Test
    void contextLoads() {
    }

}
