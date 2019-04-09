package com.xueyuan.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xueyuan.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyuan.eduservice.entity.QueryTeacher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author lucy
 * @since 2019-03-19
 */
public interface EduTeacherService extends IService<EduTeacher> {

    void pageList(Page<EduTeacher> pageTeacher, QueryTeacher queryTeacher);

    boolean deletId(String id);

    String upload(MultipartFile file);
}
