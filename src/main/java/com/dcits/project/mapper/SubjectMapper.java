package com.dcits.project.mapper;

import com.dcits.project.pojo.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SubjectMapper {
    int deleteByPrimaryKey(String subjectSerial);

    int insert(Subject record);

    Subject selectByPrimaryKey(String subjectSerial);

    List<Subject> selectAll();

    int updateByPrimaryKey(Subject record);
}