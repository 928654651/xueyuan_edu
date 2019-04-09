package com.xueyuan.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.xueyuan.eduservice.entity.EduSubject;
import com.xueyuan.eduservice.entity.dto.SubjectOneLevel;
import com.xueyuan.eduservice.entity.dto.SubjectTwoLevel;
import com.xueyuan.eduservice.handler.EduException;
import com.xueyuan.eduservice.mapper.EduSubjectMapper;
import com.xueyuan.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.ExcelImportHSSFUtil;

import javax.management.Query;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author lucy
 * @since 2019-03-25
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //导入分类数据
    //poi读取excel
    @Override
    public List<String> importData(MultipartFile file) {

        List<String> msg = new ArrayList<>();
        try {
            //1 获取文件输入流
            InputStream in = file.getInputStream();

            //2 创建workbook
//            Workbook workbook = new HSSFWorkbook(in);
//            Sheet sheetAt = workbook.getSheetAt(0);
            //3 获取sheet
            //使用工具类实现
            ExcelImportHSSFUtil importHSSFUtil = new ExcelImportHSSFUtil(in);
            HSSFSheet sheet = importHSSFUtil.getSheet();

            //4 获取sheet里面所有的行（动态获取到）
            //从第二行开始获取数据，添加到表里面

            //获取最后一行数据索引位置，从0开始的
            int count = sheet.getLastRowNum();
            //int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            //从第二行开始获取数据
            //因为索引从0开始的，从第二行获取值，索引1
            for (int i = 1; i <=count; i++) {
                //遍历获取每一行
                HSSFRow row = sheet.getRow(i);
                //如果行没有数据，返回错误信息
                if(row == null) {
                    msg.add("表格数据为空，请输入数据");
                    return msg;
                }

                //5 如果行有数据，获取行里面的列（固定的值）
                //获取第一列
                HSSFCell cellOne = row.getCell(0);
                if(cellOne == null) {
                    //错误信息
                    msg.add("第"+i+"行数据为空");
                    continue;//跳出当前循环
                }
                //获取第一列列里面的值
                //参数：Cell cell, int cellType
                //获取类里面数据类型
                int cellOneType = cellOne.getCellType();
                //调用工具类的方法获取第一列里面的值
                String oneValue = importHSSFUtil.getCellValue(cellOne, cellOneType);

                /*
                * 判断一级分类在数据库表是否存在，如果不存在添加，如果存在不添加
                * */
                String parentid = null;
                EduSubject existEduSubject = this.existOneSubject(oneValue);
                if(existEduSubject == null) {//不存在一级分类
                    //添加一级分类
                    EduSubject eduSubjectOne = new EduSubject();
                    eduSubjectOne.setTitle(oneValue);
                    eduSubjectOne.setParentId("0");
                    eduSubjectOne.setSort(0);
                    baseMapper.insert(eduSubjectOne);
                    //获取一级分类id，作为二级分类parentid值
                    parentid = eduSubjectOne.getId();
                } else {//存在一级分类
                    //获取一级分类id，作为二级分类parentid值
                    parentid = existEduSubject.getId();
                }

                //获取第二列
                HSSFCell cellTwo = row.getCell(1);
                if(cellTwo == null) {
                    //错误信息
                    msg.add("第"+i+"行数据为空");
                    continue;//跳出当前循环
                }

                //获取第二列数据类型
                int cellTwoType = cellTwo.getCellType();
                //调用工具类的方法获取第二列里面的值
                String twoValue = importHSSFUtil.getCellValue(cellTwo, cellTwoType);

                /*
                * 判断二级分类在数据库表是否存在，如果存在添加
                * */
                EduSubject existTwoEduSubject = this.existTwoSubject(twoValue, parentid);
                if(existTwoEduSubject == null) {//二级分类不存在
                    //添加二级分类
                    EduSubject eduSubjectTwo = new EduSubject();
                    eduSubjectTwo.setTitle(twoValue);
                    eduSubjectTwo.setParentId(parentid);
                    eduSubjectTwo.setSort(0);
                    baseMapper.insert(eduSubjectTwo);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw new EduException(20001,"导入数据失败");
        }
        return msg;
    }

    //返回要求的格式的json数据
    // [{"id":"1","title":"前端",children:[{"id":"11","title":"js"},{"id":"12","title":"html"}]}]
    @Override
    public List<SubjectOneLevel> getListSubject() {
        //1 获取所有一级分类
        List<EduSubject> oneLevelAll = this.getOneLevelAll();

        //创建list集合，用于一级分类dto对象封装
        List<SubjectOneLevel> subjectOneLevelList = new ArrayList<>();
        //把返回所有的一级分类转换成dto SubjectOneLevel对象
        for (int i = 0; i < oneLevelAll.size(); i++) {
            //得到每个一级分类对象
            EduSubject oneSubject = oneLevelAll.get(i);
            //把oneSubject对象转换SubjectOneLevel对象
            SubjectOneLevel subjectOneLevel = new SubjectOneLevel();
            BeanUtils.copyProperties(oneSubject,subjectOneLevel);
            //把转换之后的对象放到list集合里面
            subjectOneLevelList.add(subjectOneLevel);

            //获取所有二级分类
            List<EduSubject> twoLevelAll = this.getTwoLevelAll();
            //创建集合存储转换之后二级分类dto对象
            List<SubjectTwoLevel> subjectTwoLevelList = new ArrayList<>();
            //遍历所有的二级分类，得到每个二级分类转换二级分类dto对象
            for (int m = 0; m < twoLevelAll.size(); m++) {
                //得到每个二级分类
                EduSubject twoSubject = twoLevelAll.get(m);
                //判断当前二级分类是否是上面一级分类里面的
                //判断二级分类parentid和一级分类id是否一样
                if(twoSubject.getParentId().equals(oneSubject.getId())) {
                    //把查询出来的每个二级分类对象转换二级分类dto对象
                    SubjectTwoLevel subjectTwoLevel = new SubjectTwoLevel();
                    BeanUtils.copyProperties(twoSubject,subjectTwoLevel);
                    //放到二级分类dto集合里面
                    subjectTwoLevelList.add(subjectTwoLevel);
                }
            }
            subjectOneLevel.setChildren(subjectTwoLevelList);
        }
        return subjectOneLevelList;
    }

    @Override
    public boolean deleteById(String id) {
        //判断一级分类里面是否有二级分类
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0) {
            return false;
        } else {
            int result = baseMapper.deleteById(id);
            return result>0;
        }
    }

    //添加一级分类
    @Override
    public boolean addLevelOne(EduSubject eduSubject) {
        //判断一级分类是否存在，不存在进行添加
        EduSubject existSubject = this.existOneSubject(eduSubject.getTitle());
        if(existSubject == null) {//不存在
            //一级分类parentId是0
            eduSubject.setParentId("0");
            //添加
            int count = baseMapper.insert(eduSubject);
            return count>0;
        } else {
            throw new EduException(20001,"一级分类已经存在");
        }
    }

    //添加二级分类
    @Override
    public boolean addLevelTwo(EduSubject eduSubject) {
        //判断二级分类是否存在，如果不存在添加
        EduSubject existTwoSubject = this.existTwoSubject(eduSubject.getTitle(), eduSubject.getParentId());
        if(existTwoSubject == null) {//不存在
            //添加二级分类
            int count = baseMapper.insert(eduSubject);
            return count>0;
        } else {
            //存在
            throw new EduException(20001,"二级分类已经存在");
        }
    }

    //获取所有一级分类
    private List<EduSubject> getOneLevelAll() {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id","0");
        List<EduSubject> eduSubjects = baseMapper.selectList(wrapper);
        return eduSubjects;
    }

    ////获取所有二级分类
    private List<EduSubject> getTwoLevelAll() {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        //eq表示=     ne表示!=
        wrapper.ne("parent_id","0");
        List<EduSubject> eduSubjects = baseMapper.selectList(wrapper);
        return eduSubjects;
    }

    //判断二级分类在数据库表是否存在
    private EduSubject existTwoSubject(String twoTypeName,String parentid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",twoTypeName);
        wrapper.eq("parent_id",parentid);
        return baseMapper.selectOne(wrapper);
    }

    //判断一级分类是否存在
    private EduSubject existOneSubject(String oneTypeName) {
        //根据一级分类名称进行查询
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",oneTypeName);
        //根据parent_id=0判断一级分类
        wrapper.eq("parent_id","0");
        EduSubject eduSubject = baseMapper.selectOne(wrapper);
        return eduSubject;
    }
}
