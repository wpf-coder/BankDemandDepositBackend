package com.dcits.project.mapper;

import com.dcits.project.pojo.LedgerRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
@Repository
@Mapper
public interface LedgerRecordMapper {
    int deleteByPrimaryKey(Integer ledgerRecordSerial);

    int insert(LedgerRecord record);

    LedgerRecord selectByPrimaryKey(Integer ledgerRecordSerial);

    List<LedgerRecord> selectAll();

    int updateByPrimaryKey(LedgerRecord record);

    List<LedgerRecord> ledgerCorrespondingFlow(@Param("yesterday")Date ledgerDate,
                                               @Param("today")Date flowDate,
                                               @Param("subject")String ledgerSubject,
                                               @Param("type")int flowType);

    List<LedgerRecord> queryCashLedger(
            @Param("branchId")String branchId,@Param("type")String type,
            @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime);
}