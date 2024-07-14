package com.dcits.project.mapper;

import com.dcits.project.pojo.Teller;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface TellerMapper {
    int deleteByPrimaryKey(String tellerId);

    int insert(Teller record);

    Teller selectByPrimaryKey(String tellerId);

    List<Teller> selectAll();

    int updateByPrimaryKey(Teller record);
}