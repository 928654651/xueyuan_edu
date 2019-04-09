package com.xueyuan.eduservice.controller;

import com.xueyuan.eduservice.entity.EduTeacher;
import com.xueyuan.eduservice.service.EduTeacherService;
import com.xueyuan.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/eduservice/oss")
@CrossOrigin
public class FileUploadController {

    @Autowired
    private EduTeacherService eduTeacherService;

    //头像上传的方法
    @PostMapping("upload")
    public R uploadImage(@RequestParam("file") MultipartFile file){

        //调用servicee的方法，返回上传到oss的路径
        String url = eduTeacherService.upload(file);

        return R.ok().data("uploadUrl",url);
    }

}
