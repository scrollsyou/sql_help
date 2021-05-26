package com.example;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.vo.SchoolVo;
import com.gugusong.sqlmapper.db.ExampleImpl;
import com.gugusong.sqlmapper.springboot.EnableSqlHelp;
import com.gugusong.sqlmapper.springboot.SqlHelpBaseDao;

@RestController
@EnableAutoConfiguration
@EnableSqlHelp
public class MyApplication {
	
	@Resource
	private SqlHelpBaseDao baseDao;

	@RequestMapping("/")
	List<SchoolVo> getSchool() {
        return baseDao.findAll(ExampleImpl.newInstance().gt("school.id", 0), SchoolVo.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}
