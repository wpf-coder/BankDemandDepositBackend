package com.dcits.project.mapper;


import com.dcits.project.pojo.SystemInformation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SystemInformationMapper {
    int deleteByPrimaryKey(String systemSerial);

    int insert(SystemInformation record);

    SystemInformation selectByPrimaryKey(String systemSerial);

    List<SystemInformation> selectAll();

    int updateByPrimaryKey(SystemInformation record);
}