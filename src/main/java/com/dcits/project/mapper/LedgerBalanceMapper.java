package com.dcits.project.mapper;

import com.dcits.project.pojo.LedgerBalance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface LedgerBalanceMapper {
    List<LedgerBalance> getLedgerAndBranchCorrespondingLedgerFLow(@Param("date")Date date,
                                                                  @Param("diff")int diff);

    boolean backupOneRecord(@Param("prototype")LedgerBalance ledgerBalance,
                            @Param("date")  Date backupDate);

    boolean insertDefaultValue(LedgerBalance balance);

    int deleteByPrimaryKey(@Param("ledgerBalanceSubjectSerial") String ledgerBalanceSubjectSerial,
                           @Param("ledgerBalanceBalance") BigDecimal ledgerBalanceBalance);
                           //@Param("ledgerBalanceAccount") String ledgerBalanceAccount);



    int insert(LedgerBalance record);

    LedgerBalance selectByPrimaryKey(@Param("ledgerBalanceSubjectSerial") String ledgerBalanceSubjectSerial,
                                     @Param("ledgerBalanceBalance") BigDecimal ledgerBalanceBalance);
                                     //@Param("ledgerBalanceAccount") String ledgerBalanceAccount);

    List<LedgerBalance> selectAll();

    int updateByPrimaryKey(LedgerBalance record);
}