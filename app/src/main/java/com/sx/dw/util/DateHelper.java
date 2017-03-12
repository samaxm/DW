package com.sx.dw.util;

import java.util.Calendar;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/5 10:31
 */

public class DateHelper {

    public static long getCurrentDateNum(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }
}
