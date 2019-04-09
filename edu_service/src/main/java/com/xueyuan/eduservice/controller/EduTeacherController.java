package com.xueyuan.eduservice.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xueyuan.eduservice.entity.EduTeacher;
import com.xueyuan.eduservice.entity.QueryTeacher;
import com.xueyuan.eduservice.service.EduTeacherService;
import com.xueyuan.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author lucy
 * @since 2019-03-19
 */
@RestController
@RequestMapping("/eduservice/teacher")
@CrossOrigin
public class EduTeacherController {

    @Autowired
    private EduTeacherService eduTeacherService;

    //1 查询所有讲师信息，返回
//    @GetMapping("list")
//    public List<EduTeacher> getList() {
//        List<EduTeacher> list = eduTeacherService.list(null);
//        return list;
//    }

    //登录的方法
    @PostMapping("login")
    public R login(){

        return R.ok().data("token","admin");
    }

    //info方法获取登录信息
    @GetMapping("info")
    public R info(){
        return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
    }



    @GetMapping("list")
    public R getList() {
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("items",list);
    }

    //2 逻辑删除
    @DeleteMapping("{id}")
    public R removeTeacherById(@PathVariable String id) {
//        boolean flag = eduTeacherService.removeById(id);
//        if(flag) {
//            return R.ok();
//        } else {
//            return R.error();
//        }
        boolean flag = eduTeacherService.deletId(id);
        if(flag){
            return R.ok();
        }else {
            return R.error();
        }
    }

    //3 简单分页查询
    @GetMapping("{page}/{limit}")
    public R getListPage(@PathVariable Long page,
                         @PathVariable Long limit) {
        //创建Page对象
        Page<EduTeacher> pageTeacher = new Page<>(page,limit);
        //调用方法得到结果
        eduTeacherService.page(pageTeacher, null);
        //从pageTeacher对象里面获取需要的数据
        List<EduTeacher> records = pageTeacher.getRecords();
        long total = pageTeacher.getTotal();

        return R.ok().data("total",total).data("items",records);
    }

    //4 多条件查询带分页
    @PostMapping("pageList/{page}/{limit}")
    public R getPageCondition(@PathVariable Long page,
                              @PathVariable Long limit,
                              @RequestBody(required = false) QueryTeacher queryTeacher) {
        Page<EduTeacher> pageTeacher = new Page<>(page,limit);
        //在service自己创建方法，做条件操作
        eduTeacherService.pageList(pageTeacher,queryTeacher);
        //从pageTeacher对象里面获取需要的数据
        List<EduTeacher> records = pageTeacher.getRecords();
        long total = pageTeacher.getTotal();

        return R.ok().data("total",total).data("items",records);
    }

    //5 添加讲师
    @PostMapping("save")
    public R addTeacher(@RequestBody EduTeacher eduTeacher) {
        boolean save = eduTeacherService.save(eduTeacher);
        if(save) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //6 根据id查询的方法
    @GetMapping("{id}")
    public R getTeacherById(@PathVariable String id) {
        EduTeacher eduTeacher = eduTeacherService.getById(id);
        return R.ok().data("eduTecher",eduTeacher);
    }

    //7 修改操作的方法
    @PostMapping("updateTeacher/{id}")
    public R updateTeacherById(@PathVariable String id,
                               @RequestBody EduTeacher eduTeacher) {
        eduTeacher.setId(id);
        boolean save = eduTeacherService.updateById(eduTeacher);
        if(save) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}

