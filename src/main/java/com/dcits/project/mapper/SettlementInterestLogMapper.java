package com.dcits.project.mapper;

import com.dcits.project.pojo.SettlementInterestLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface SettlementInterestLogMapper {
    int deleteByPrimaryKey(Integer settlementInterestLogSerial);

    int insert(SettlementInterestLog record);

    SettlementInterestLog selectByPrimaryKey(Integer settlementInterestLogSerial);

    List<SettlementInterestLog> selectAll();

    int updateByPrimaryKey(SettlementInterestLog record);
}