package com.holarin.monefy;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Это модель приложения, здесь есть дата начала учёта и дата конца учёта максимальный бюджет
 * */
public class MonefyModel {
    public Calendar start;
    public Calendar end;
    public double maxMoney;


    MonefyModel() {
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        start.set(Calendar.MONTH, start.get(Calendar.MONTH) + 1);
        start.set(Calendar.DAY_OF_MONTH, start.getActualMinimum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.MONTH, end.get(Calendar.MONTH) + 1);
        this.start = start;
        this.end = end;
        maxMoney = 5000; // <-Вот кстати стандартный бюджет, можно увеличить здесь
    }
}
