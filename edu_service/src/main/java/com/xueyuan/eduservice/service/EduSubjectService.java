package com.xueyuan.eduservice.service;

import com.xueyuan.eduservice.entity.EduSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xueyuan.eduservice.entity.dto.SubjectOneLevel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author lucy
 * @since 2019-04-01
 */
public interface EduSubjectService extends IService<EduSubject> {

    List<String> importData(MultipartFile file);

    List<SubjectOneLevel> getListSubject();

    boolean deleteById(String id);

    boolean addLevelOne(EduSubject eduSubject);

    boolean addLevelTwo(EduSubject eduSubject);
}
