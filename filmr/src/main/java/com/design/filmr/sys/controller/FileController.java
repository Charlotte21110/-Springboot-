package com.design.filmr.sys.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.design.filmr.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Api(tags = "文件操作")
@RestController
@RequestMapping("/file")
public class FileController {


    // 文件储存位置
    private static final String IMG_PATH = System.getProperty("user.dir") + File.separator + "DBImg";

    @ApiOperation("图片上传")
    @PostMapping("/upload")
    public Result<?> upload(MultipartFile file) throws IOException{
        String originFileName = file.getOriginalFilename(); // eg. userAvatar.png
        String mainName = FileUtil.mainName(originFileName); // userAvatar
        String exName = FileUtil.extName(originFileName); // png


        if(!FileUtil.exist(IMG_PATH)){
            // 当DBImg文件夹不存在时创建
            FileUtil.mkdir(IMG_PATH);
        }

        // 如果文件存在则重命名文件并保存文件
        if(FileUtil.exist(IMG_PATH + File.separator + originFileName)){
            originFileName = System.currentTimeMillis() + "_" +mainName + "." + exName;
        }

        file.transferTo(new File(IMG_PATH + File.separator + originFileName));

        String url = "http://localhost:9999/file/download/" + originFileName;
        return Result.success(url, "图片上传成功");
    }

    @ApiOperation("下载存储图片")
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException{
        // 不存在则直接结束
        String filePath = IMG_PATH + File.separator + fileName;
        if(!FileUtil.exist(filePath)){
            return;
        }

        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    @ApiOperation("影评图片上传")
    @PostMapping("/editorUpload")
    public Dict editorUpload(MultipartFile file) throws IOException{
        String originFileName = file.getOriginalFilename(); // eg. userAvatar.png
        String mainName = FileUtil.mainName(originFileName); // userAvatar
        String exName = FileUtil.extName(originFileName); // png


        if(!FileUtil.exist(IMG_PATH)){
            // 当DBImg文件夹不存在时创建
            FileUtil.mkdir(IMG_PATH);
        }

        // 如果文件存在则重命名文件并保存文件
        if(FileUtil.exist(IMG_PATH + File.separator + originFileName)){
            originFileName = System.currentTimeMillis() + "_" +mainName + "." + exName;
        }

        file.transferTo(new File(IMG_PATH + File.separator + originFileName));

        String url = "http://localhost:9999/file/download/" + originFileName;

        Dict dict = Dict.create();
        dict.set("errno", 0);
        dict.set("data", CollUtil.newArrayList(Dict.create().set("url", url)));
        return dict;
    }
}
