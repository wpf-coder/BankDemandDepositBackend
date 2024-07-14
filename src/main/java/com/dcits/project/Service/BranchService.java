package com.dcits.project.service;

import com.dcits.project.exception.RequestHandleException;
import com.dcits.project.mapper.LedgerRecordMapper;
import com.dcits.project.pojo.Branch;
import com.dcits.project.pojo.LedgerRecord;
import com.dcits.project.pojo.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BranchService {
    @Autowired
    LedgerRecordMapper ledgerRecordMapper;

    public String getOnlineBranchId(){
        return Branch.ONLINE_ID;

    }


     /**
      * 现金总账查询
     **/
    public List<LedgerRecord> queryCashLedger(String branchId, String type, Timestamp startTime, Timestamp endTime) {

        if ( type.equals("0") || Subject.validateSubjectSerial(type)) {
            return ledgerRecordMapper.queryCashLedger(branchId,type, startTime, endTime);
        } else {
            throw new RequestHandleException(1, "暂不支持除现金总账之外其它总账记录查询");
        }
    }


}
