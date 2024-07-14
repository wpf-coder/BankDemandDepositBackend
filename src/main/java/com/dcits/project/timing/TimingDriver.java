package com.dcits.project.timing;

import com.dcits.project.service.SystemInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;

@Component
@Transactional
@EnableScheduling
public class TimingDriver {
    @Autowired
    private SystemInformationService systemInformationService;

    @Autowired
    private InterestHandle interestHandle;
    @Autowired
    private LedgerFlowHandle ledgerFlowHandle;


//    @Scheduled(cron = "0 ${system.timing.min} ${system.timing.hour} * * *")
    @Scheduled(cron = "0 0 0 * * *")
    public void timingDriver(){
        Calendar systemCalendar = systemInformationService.getSystemCalendar();
        //累计计息 与 结息
        interestHandle.handleInterest(systemCalendar);

        //开始现金累计。
        ledgerFlowHandle.updateBalanceOfLedger(systemCalendar);

        //定时任务最后 翻新 会计日。
        systemInformationService.updateSystemTime();
    }
}
