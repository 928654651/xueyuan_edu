package com.xueyuan.eduservice.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xueyuan.eduservice.entity.EduTeacher;
import com.xueyuan.eduservice.entity.QueryTeacher;
import com.xueyuan.eduservice.handler.EduException;
import com.xueyuan.eduservice.handler.OSSValueUtils;
import com.xueyuan.eduservice.mapper.EduTeacherMapper;
import com.xueyuan.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author lucy
 * @since 2019-03-19
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {


    //条件查询带分页
    @Override
    public void pageList(Page<EduTeacher> pageTeacher, QueryTeacher queryTeacher) {

       /* //模拟异常，为了测试，以后删除的
        try {
            int a = 9/0;
        }catch(Exception e) {
            //抛出自定义异常
            throw new EduException(20001,"执行了自定义异常");
        }*/


        //构建条件
        if(queryTeacher == null) {
            //条件对象为空，查询全部
            baseMapper.selectPage(pageTeacher,null);
        }
        //从条件对象里面把条件值获取出来
        String name = queryTeacher.getName();//姓名
        Integer level = queryTeacher.getLevel();//头衔
        String begin = queryTeacher.getBegin();//开始时间
        String end = queryTeacher.getEnd();//结束时间

        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        //判断条件值是否为空
        if(!StringUtils.isEmpty(name)) {//姓名不为空
            // like '%z%'
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(level)) {
            wrapper.eq("level",level);
        }
        if(!StringUtils.isEmpty(begin)) {
            wrapper.ge("gmt_create",begin);
        }
        if(!StringUtils.isEmpty(end)) {
            wrapper.le("gmt_create",end);
        }

        baseMapper.selectPage(pageTeacher,wrapper);
    }

    //根据id删除
    @Override
    public boolean deletId(String id) {
        Integer rows = baseMapper.deleteById(id);
        //删除成功rows值1    1>0
        //删除失败rows值0    0>0
        return rows > 0;

    }

    //上传文件到oss，返回上传文件的路径
    @Override
    public String upload(MultipartFile file) {

        String endpoint = OSSValueUtils.END_POINT;
        String id = OSSValueUtils.ACCESS_KEY_ID;
        String security = OSSValueUtils.ACCESS_KEY_SECRET;
        String butketname = OSSValueUtils.BUCKET_NAME;


        try {
            //1 获取文件输入流
            InputStream in = file.getInputStream();

            //2 创建oss服务对象
            OSSClient ossClient = new OSSClient(endpoint, id, security);


            //上传文件名
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + file.getOriginalFilename();

            // 2019/03/23
            String filePath = new DateTime().toString("yyyy/MM/dd");

            // 2019/03/23/wererewaf1.jpg
            String path = filePath + "/" + filename;
            //3 调用putObject方法实现

            ossClient.putObject(butketname, path, in);
            //关闭服务
            ossClient.shutdown();

            //4 返回上传的oss的路径
            //http://edu-demo123.oss-cn-beijing.aliyuncs.com/avatar/2019/03/22/1a48123b-619a-4373-99a1-8eab07253ce1.png
            String url = "http://"+butketname+"."+endpoint+"/"+path;

            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


















