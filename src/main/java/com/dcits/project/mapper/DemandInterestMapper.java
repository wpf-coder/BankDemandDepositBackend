package com.dcits.project.mapper;

import com.dcits.project.pojo.DemandInterest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Mapper
@Repository
public interface DemandInterestMapper {
    int deleteByPrimaryKey(String demandInterestCardId);

    int insert(DemandInterest record);

    DemandInterest selectByPrimaryKey(String demandInterestCardId);


    List<DemandInterest> selectAll();

    int updateByPrimaryKey(DemandInterest record);


}