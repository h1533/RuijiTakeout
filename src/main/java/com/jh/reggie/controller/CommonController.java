package com.jh.reggie.controller;

import com.jh.reggie.commons.ErrorCode;
import com.jh.reggie.commons.R;
import com.jh.reggie.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-09 17:46:06
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传文件
     *
     * @param file
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 判断文件是否存在
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND_ERROR.getMessage());
        }
        // 修改源文件名称
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf(".")); // .png
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newFileName = uuid + suffixName;
        // 新建目录或文件
        File dirFile = new File(basePath + newFileName);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new CustomException(ErrorCode.DIR_ERROR.getMessage());
            }
        }
        // 将临时文件转存到指定位置
        try {
            file.transferTo(dirFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(newFileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            // 输入流，通过输入流读取文件内容
            FileInputStream inputStream = new FileInputStream(basePath + name);
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            //  关闭流
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
