package com.dcits.project.mapper;

import com.dcits.project.pojo.TransactionFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TransactionFlowMapper {
    int deleteByPrimaryKey(Integer transactionFlowSerial);

    boolean insert(TransactionFlow record);
    List<TransactionFlow> queryTransactionFlow(
            @Param("id")String id,@Param("type") int type,
            @Param("startTime") Timestamp startTime,@Param("endTime") Timestamp endTime);

}