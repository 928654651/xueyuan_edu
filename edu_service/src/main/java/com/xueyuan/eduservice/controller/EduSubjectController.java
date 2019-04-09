package com.xueyuan.eduservice.controller;


import com.xueyuan.eduservice.entity.EduSubject;
import com.xueyuan.eduservice.entity.dto.SubjectOneLevel;
import com.xueyuan.eduservice.service.EduSubjectService;
import com.xueyuan.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author lucy
 * @since 2019-04-01
 */
@RestController
@RequestMapping("/eduservice/subject")
@CrossOrigin
public class EduSubjectController {

    @Autowired
    private EduSubjectService eduSubjectService;

    //添加二级分类
    @PostMapping("addTwo")
    public R addTwoLevel(@RequestBody EduSubject eduSubject){
        boolean flag = eduSubjectService.addLevelTwo(eduSubject);
        if(flag){
            return R.ok().message("添加二级分类成功");
        }else {
            return R.error().message("添加二级分类失败");
        }
    }

    //添加一级分类
    @PostMapping("addOne")
    public R addOneLevel(@RequestBody EduSubject eduSubject){
        boolean flag = eduSubjectService.addLevelOne(eduSubject);
        if(flag){
            return R.ok().message("添加一级分类成功");
        }else {
            return R.error().message("添加一级分类失败");
        }
    }

    //删除方法
    @DeleteMapping("{id}")
    public R deleteById(@PathVariable String id){
        boolean flag = eduSubjectService.deleteById(id);
        if (flag){
            return R.ok();
        }else {
            return R.error();
        }
    }

    //返回所有分类数据  按照要求格式返回
    //dto方式
    @GetMapping
    public R getSubjectList(){
        List<SubjectOneLevel> list = eduSubjectService.getListSubject();
        return R.ok().data("items", list);
    }

    //1 批量导入课程分类 excel表格
    @PostMapping("import")
    public R imoortSubject(@RequestParam("file")MultipartFile file){

        /*
        * 返回集合：
        * 因为excel表格有很多行数据，导入时候可能有某些汉行出现问题，
        * 返回错误信息，比如第一行出错了，第三行出错了
        * 把多个错误信息放到list集合中，前端获取时候方便
        * */
        //返回list集合里面，存储错误信息
        List<String> list = eduSubjectService.importData(file);
        //判断返回list集合是否有数据
        if(list.size() == 0){//成功
            return R.ok().message("导入分类数据成功");
        }else {//返回错误信息list集合
            return R.error().message("部分数据导入失败").data("messageUrl",list);
        }
    }
}

