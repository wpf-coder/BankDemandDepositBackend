package com.dcits.project.mapper;

import com.dcits.project.pojo.DemandInterest;
import com.dcits.project.pojo.InterestRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Mapper
@Repository
public interface InterestRateMapper {
    int deleteByPrimaryKey(Integer interestRateSerial);

    int insert(InterestRate record);

    InterestRate selectByPrimaryKey(Integer interestRateSerial);

    InterestRate selectByCodeAndTime(@Param("code")String code, @Param("time") Date date);



    int updateByPrimaryKey(InterestRate record);


}