package com.dcits.project.pojo;

import com.dcits.project.service.SystemInformationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SystemInformation {
    private String systemSerial;

    private Date systemTime;

    private transient Calendar calendar;

    public void jumpNextDay(){
        this.calendar.add(Calendar.DAY_OF_MONTH,1);
        this.systemTime = calendar.getTime();
    }

    /**
     * 为了日历与date同步而特别设置是setter
     * @how:
     * @return:
     *       void
    **/
    public void setSystemTime(Date systemTime) {
        this.systemTime = systemTime;
        this.calendar = new Calendar.Builder()
                .setInstant(this.systemTime)
                .build();
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.systemTime = calendar.getTime();
    }


    public void setSystemSerial(String systemSerial) {
        this.systemSerial = systemSerial;
    }
}