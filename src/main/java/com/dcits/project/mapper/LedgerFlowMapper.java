package com.dcits.project.mapper;

import com.dcits.project.pojo.LedgerFlow;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LedgerFlowMapper {
    int deleteByPrimaryKey(Integer ledgerFlowSerial);

    boolean insert(LedgerFlow record);

    LedgerFlow selectByPrimaryKey(Integer ledgerFlowSerial);

    List<LedgerFlow> selectAll();

    int updateByPrimaryKey(LedgerFlow record);
}